package pt.isel.ls.repository.database

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.database.utils.*
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Activity.Duration
import pt.isel.ls.utils.*
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement

class ActivityDBRepository(private val dataSource: PGSimpleDataSource, val suffix: String) : ActivityRepository {

    private val activityTable = "activity$suffix"

    /**
     * Creates a new activity using the parameters received
     *
     * @param date the activity date
     * @param duration the activity duration
     * @param sportID the activity sport ID
     * @param routeID the activity route ID
     * @param userID the activity user ID
     */
    override fun addActivity(
        date: LocalDate,
        duration: Duration,
        sportID: SportID,
        routeID: RouteID?,
        userID: UserID
    ): ActivityID
        = dataSource.connection.transaction {
            val query = """INSERT INTO $activityTable (date, duration, sport, route, "user")VALUES (?, ?, ?, ?, ?)"""
            val pstmt =  prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            pstmt.use { ps ->
                ps.apply {
                    setActivity( date, duration, sportID, routeID, userID)
                    executeUpdate()
                }.generatedKey()
            }
        }


    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [Activity] that were created by the given user
     */
    override fun getActivitiesByUser(userID: UserID): List<Activity> {
        dataSource.connection.transaction {
            val query = """SELECT * FROM $activityTable WHERE "user" = ?"""
            val pstmt = prepareStatement(query)
            pstmt.use { ps ->
                ps.apply {
                    setInt(1, userID.toInt())
                }
                val rs = ps.executeQuery()
                return rs.toListOf(ResultSet::toActivity)
            }
        }
    }



    /**
     * Gets the activities that match the given sport id, date, route id
     * and orders it by the given orderBy parameter.
     *
     * @param sid sport identifier
     * @param orderBy order by duration time,
     * this parameter only has two possible values [Order.ASCENDING] or [Order.DESCENDING]
     * @param date activity date (optional)
     * @param rid route identifier (optional)
     *
     * @return [List] of [Activity]
     */
    override fun getActivities(sid: SportID, orderBy: Order, date: LocalDate?, rid: RouteID?): List<Activity> {
        dataSource.connection.transaction {
            val (query, hasDate) = getActivitiesQueryBuilder(date, rid, orderBy)
            val pstmt = prepareStatement(query)
            val ridIdx = if (hasDate) 3 else 2
            pstmt.use { ps ->
                ps.apply {

                    setInt(1, sid.toInt())

                    date?.let {
                        setDate(2, Date.valueOf(it.toJavaLocalDate()))
                    }

                    rid?.toIntOrNull()?.let { rid->
                        setInt(ridIdx, rid)
                    }

                }
                val rs = ps.executeQuery()
                return rs.toListOf(ResultSet::toActivity)
            }
        }
    }

    /**
     * Builds the query to get the activities based on the given parameters.
     * @return the query and if the query has a date parameter
     */
    private fun getActivitiesQueryBuilder(date: LocalDate?, rid: RouteID?, orderBy: Order): Pair<String, Boolean>{
        val sb = StringBuilder()
        sb.append(" SELECT * FROM $activityTable WHERE sport = ? ")
        if (date != null) {
            sb.append("AND date = ? ")
        }
        if (rid != null) {
            sb.append("AND route = ? ")
        }
        sb.append("ORDER BY duration ${if (orderBy == Order.ASCENDING) "ASC" else "DESC"}")
        return Pair(sb.toString(), date != null)
    }

    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    override fun deleteActivity(activityID: ActivityID): Boolean {
        dataSource.connection.transaction {
            val query = """DELETE FROM $activityTable WHERE id = ?"""
            prepareStatement(query).use { ps ->
                ps.setInt(1, activityID.toInt())
                return ps.executeUpdate() == 1
            }
        }
    }

    /**
     * Gets the activity that matches the given unique activity identifier.
     *
     * @param activityID the identifier of the activity to get
     * @return [Activity] if the id exists or null if it doesn't
     */
    override fun getActivity(activityID: ActivityID): Activity? =
        queryActivityByID(activityID){ rs ->
            rs.ifNext { rs.toActivity() }
        }

    /**
     * Checks if the activity identified by the given identifier exists.
     * @param activityID the id of the activity to check
     * @return [Boolean] true if it exists
     */
    override fun hasActivity(activityID: ActivityID): Boolean =
        queryActivityByID(activityID){ rs: ResultSet ->
            rs.next()
        }

    /**
     * Makes a query to get an activity by its identifier.
     *
     * @param activityID The id of the sport to be queried.
     * @param block specifies what the caller wants to do with the result set.
     * @return [T] The result of calling the block function.
     */
    private fun <T> queryActivityByID(activityID: ActivityID, block: (ResultSet) -> T): T =
        dataSource.connection.transaction {
            val query = """SELECT * FROM $activityTable WHERE id = ?"""
            val pstmt = prepareStatement(query)
            pstmt.use { ps ->
                ps.setInt(1, activityID.toInt())
                val resultSet: ResultSet = ps.executeQuery()
                resultSet.use { block(it) }
            }
        }

}



