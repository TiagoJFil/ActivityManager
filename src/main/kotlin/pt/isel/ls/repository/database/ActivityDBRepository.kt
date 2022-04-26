package pt.isel.ls.repository.database

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.service.entities.User
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.Order
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.repository.applyPagination
import pt.isel.ls.utils.repository.generatedKey
import pt.isel.ls.utils.repository.ifNext
import pt.isel.ls.utils.repository.queryTableByID
import pt.isel.ls.utils.repository.setActivity
import pt.isel.ls.utils.repository.toActivity
import pt.isel.ls.utils.repository.toListOf
import pt.isel.ls.utils.repository.toUser
import pt.isel.ls.utils.repository.transaction
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class ActivityDBRepository(private val dataSource: DataSource, suffix: String) : ActivityRepository {

    private val activityTable = "activity$suffix"
    private val userTable = "user$suffix"
    private val emailTable = "email$suffix"
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
    ): ActivityID =
        dataSource.connection.transaction {
            val query = """INSERT INTO $activityTable (date, duration, sport, route, "user") VALUES (?, ?, ?, ?, ?)"""
            val pstmt = prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            pstmt.use { ps ->
                ps.apply {
                    setActivity(date, duration, sportID, routeID, userID)
                    executeUpdate()
                }.generatedKey()
            }
        }

    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [Activity] that were created by the given user
     */
    override fun getActivitiesByUser(userID: UserID, paginationInfo: PaginationInfo): List<Activity> {
        dataSource.connection.transaction {
            val query = """SELECT * FROM $activityTable WHERE "user" = ? LIMIT ? OFFSET ?"""
            val pstmt = prepareStatement(query)
            pstmt.use { ps ->
                ps.apply {
                    setInt(1, userID)
                    applyPagination(paginationInfo,Pair(2,3))
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
    override fun getActivities(sid: SportID, orderBy: Order, date: LocalDate?, rid: RouteID?,paginationInfo: PaginationInfo): List<Activity> {
        dataSource.connection.transaction {
            val (query, hasDate) = getActivitiesQueryBuilder(date, rid, orderBy)
            val queryWithPaginationInfo = "$query LIMIT ? OFFSET ?"
            val pstmt = prepareStatement(queryWithPaginationInfo)
            val ridIdx = if (hasDate) 3 else 2
            val pagIdx = if(rid != null) ridIdx +1 else ridIdx -1
            pstmt.use { ps ->
                ps.apply {

                    setInt(1, sid)

                    date?.let {
                        setDate(2, Date.valueOf(it.toJavaLocalDate()))
                    }

                    rid?.let { rid ->
                        setInt(ridIdx, rid)
                    }
                    applyPagination(paginationInfo, Pair(pagIdx,pagIdx + 1))
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
    private fun getActivitiesQueryBuilder(date: LocalDate?, rid: RouteID?, orderBy: Order): Pair<String, Boolean> {
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
    override fun deleteActivity(activityID: ActivityID): Boolean =
        dataSource.connection.transaction<Boolean> {
            val query = """DELETE FROM $activityTable WHERE id = ?"""
            prepareStatement(query).use { ps ->
                ps.setInt(1, activityID)
                return ps.executeUpdate() == 1
            }
        }

    /**
     * Gets the activity that matches the given unique activity identifier.
     *
     * @param activityID the identifier of the activity to get
     * @return [Activity] if the id exists or null if it doesn't
     */
    override fun getActivity(activityID: ActivityID): Activity? =
        queryActivityByID(activityID) { rs ->
            rs.ifNext { rs.toActivity() }
        }

    /**
     * Checks if the activity identified by the given identifier exists.
     * @param activityID the id of the activity to check
     * @return [Boolean] true if it exists
     */
    override fun hasActivity(activityID: ActivityID): Boolean =
        queryActivityByID(activityID) { rs: ResultSet ->
            rs.next()
        }

    /**
     * Gets the users that have an activity matching the given sport id and route id.
     * @param sportID sport identifier
     * @param routeID route identifier
     * @return [List] of [User]
     */
    override fun getUsersBy(sportID: SportID, routeID: RouteID, paginationInfo: PaginationInfo): List<User> {
        dataSource.connection.transaction {
            val query =
                "SELECT $userTable.id ,$userTable.name, $emailTable.email " +
                "FROM $activityTable " +
                "JOIN $userTable ON ($activityTable.user = $userTable.id) " +
                "JOIN $emailTable ON ($emailTable.user = $userTable.id) " +
                "WHERE $activityTable.sport = ? AND $activityTable.route = ? " +
                "ORDER BY $activityTable.duration DESC" +
                "LIMIT ? OFFSET ?"
            prepareStatement(query).use {
                it.applyPagination(paginationInfo, indexes=Pair(3, 4))
                it.setInt(1, sportID)
                it.setInt(2, routeID)
                val rs = it.executeQuery()
                return rs.toListOf(ResultSet::toUser)
            }
        }
    }

    /**
     * Gets all existing activities.
     * @return [List] of [Activity]s
     */
    override fun getAllActivities(fromRequest: PaginationInfo): List<Activity> =
        dataSource.connection.transaction {
            val query = """SELECT * FROM $activityTable LIMIT ? OFFSET ? """
            prepareStatement(query).use { ps ->
                ps.applyPagination(fromRequest,Pair(1,2))
                val rs: ResultSet = ps.executeQuery()
                rs.toListOf<Activity>(ResultSet::toActivity)
            }
        }



    /**
     * Makes a query to get an activity by its identifier.
     *
     * @param activityID The id of the activity to be queried.
     * @param block specifies what the caller wants to do with the result set.
     * @return [T] The result of calling the block function.
     */
    private fun <T> queryActivityByID(activityID: ActivityID, block: (ResultSet) -> T): T =
        dataSource.queryTableByID(activityID, activityTable, block)

}
