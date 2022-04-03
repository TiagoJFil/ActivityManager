package pt.isel.ls.repository.database

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.database.utils.generatedKey
import pt.isel.ls.repository.database.utils.setActivity
import pt.isel.ls.repository.database.utils.transaction
import pt.isel.ls.repository.database.utils.*
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Activity.Duration
import pt.isel.ls.services.entities.Route
import pt.isel.ls.utils.*
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Types
import java.sql.Types.INTEGER


class ActivityDBRepository(private val dataSource: PGSimpleDataSource) : ActivityRepository {
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
            val query = """INSERT INTO activity (date, duration, sport, route, "user")VALUES (?, ?, ?, ?, ?)"""
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
            val query = """SELECT * FROM activity WHERE "user" = ?"""
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
     * Gets the activity that matches the given unique activity identifier.
     *
     * @param activityID the identifier of the activity to get
     * @return [Activity] if the id exists or null if it doesn't
     */
    override fun getActivity(activityID: ActivityID): Activity? {
        dataSource.connection.transaction {
            val query = """SELECT * FROM activity WHERE id = ?"""
            val pstmt = prepareStatement(query)
            pstmt.use { ps ->
                ps.apply {
                    setInt(1, activityID.toInt())
                }
                val rs = ps.executeQuery()
                 return rs.ifNext { rs.toActivity() }
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
            val (query, hasDate) = getActivitiesQueryBuilder(date, rid)
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
                val list = rs.toListOf(ResultSet::toActivity)
                return if(orderBy == Order.ASCENDING) list.sortedBy { it.duration.millis }
                else list.sortedByDescending { it.duration.millis }
            }
        }
    }

    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    override fun deleteActivity(activityID: ActivityID): Boolean {
        dataSource.connection.transaction {
            val res : Boolean
            val query = """DELETE FROM activity WHERE id = ?"""
            prepareStatement(query).use { ps ->
                ps.setInt(1, activityID.toInt())
               res = ps.executeUpdate() != 0
            }
            return res
        }
    }

    /**
     * Checks if the activity identified by the given identifier exists.
     * @param activityID the id of the activity to check
     * @return [Boolean] true if it exists
     */
    override fun hasActivity(activityID: ActivityID): Boolean {
        dataSource.connection.transaction {
            createStatement().use { stmt ->
                stmt.executeQuery("""SELECT * FROM "activity" WHERE id = $activityID""").use {
                        resultSet ->
                    return resultSet.next()
                }
            }
        }
    }

}

typealias HasDate = Boolean

/**
 * Builds the query to get the activities.
 */
private fun getActivitiesQueryBuilder(date: LocalDate?, rid: RouteID?): Pair<String, HasDate>{
    val sb = StringBuilder()
    sb.append("SELECT * FROM activity WHERE sport = ? ")
    if (date != null) {
        sb.append("AND date = ? ")
    }
    if (rid != null) {
        sb.append("AND route = ? ")
    }

    return Pair(sb.toString(), date != null)
}

