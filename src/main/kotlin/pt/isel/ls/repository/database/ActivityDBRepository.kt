package pt.isel.ls.repository.database

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.service.entities.Order
import pt.isel.ls.service.entities.User
import pt.isel.ls.utils.ActivityID
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
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class ActivityDBRepository(private val connection: Connection) : ActivityRepository {

    private val activityTable = "Activity"
    private val userTable = "User"
    private val emailTable = "Email"

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
    ): ActivityID {
        val query = """INSERT INTO $activityTable (date, duration, sport, route, "user") VALUES (?, ?, ?, ?, ?)"""
        val pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        return pstmt.use { ps ->
            ps.apply {
                setActivity(date, duration, sportID, routeID, userID)
                executeUpdate()
            }.generatedKey()
        }
    }

    /**
     * Updates the activity with the parameters received
     * The query must be build with the same order as the attributes in the attribute list.
     *
     * @param newDate the new activity date
     * @param newDuration the new activity duration
     * @param newRouteID the new activity route ID
     * @param activityID the activity ID
     *
     * @return [Boolean] indicating if the activity was updated
     */
    override fun updateActivity(
        newDate: LocalDate?,
        newDuration: Duration?,
        newRouteID: RouteID?,
        activityID: ActivityID,
        removeRoute: Boolean
    ): Boolean {

        val queryBuilder = StringBuilder("UPDATE $activityTable SET ")

        val attributes = listOf(newDate, newDuration, newRouteID)

        if (newDate != null)
            queryBuilder.append("date = ? ")
        if (newDuration != null) {
            if (newDate != null || newRouteID != null) queryBuilder.append(", ")
            queryBuilder.append("duration = ?::bigint ")
        }
        if (newRouteID != null || removeRoute) {
            if (newDate != null) queryBuilder.append(", ")
            queryBuilder.append("route = ? ")
        }

        queryBuilder.append("WHERE id = ?")

        val notNullAttributesCount = attributes.count { it != null }

        val activityIndex = if (removeRoute) notNullAttributesCount + 2 else notNullAttributesCount + 1

        return connection.prepareStatement(queryBuilder.toString()).use { stmt ->
            if (newDate != null)
                stmt.setDate(calculateIndex(attributes, 1), Date.valueOf(newDate.toJavaLocalDate()))
            if (newDuration != null)
                stmt.setString(calculateIndex(attributes, 2), newDuration.millis.toString())
            if (newRouteID != null) {
                stmt.setInt(calculateIndex(attributes, 3), newRouteID)
            } else {
                if (removeRoute)
                    stmt.setObject(calculateIndex(attributes, 3), null)
            }
            stmt.setInt(activityIndex, activityID)
            stmt.executeUpdate() == 1
        }
    }

    /**
     * Calculates the index to use in the prepared statement value set.
     * The attribute to check must be at least 1.
     *
     * @param attributes The list with the attributes
     * @param attributeToCheck number that represents the attribute's position in the list. Starts at 1.
     */
    private fun calculateIndex(attributes: List<Any?>, attributeToCheck: Int): Int {
        if (attributeToCheck == 1) return 1
        val startingIndex = 0
        val previousAttributes = attributes.subList(startingIndex, attributeToCheck - 1)

        return if (previousAttributes.all { it == null })
            1
        else {
            val notNullAttributes = previousAttributes.count { it != null }
            return notNullAttributes + 1
        }
    }

    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [Activity] that were created by the given user
     */
    override fun getActivitiesByUser(userID: UserID, paginationInfo: PaginationInfo): List<Activity> {
        val query = """SELECT * FROM $activityTable WHERE "user" = ? LIMIT ? OFFSET ?"""
        val pstmt = connection.prepareStatement(query)
        pstmt.use { ps ->
            ps.apply {
                setInt(1, userID)
                applyPagination(paginationInfo, Pair(2, 3))
            }
            val rs = ps.executeQuery()
            return rs.toListOf(ResultSet::toActivity)
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
    override fun getActivities(
        sid: SportID,
        orderBy: Order,
        date: LocalDate?,
        rid: RouteID?,
        paginationInfo: PaginationInfo
    ): List<Activity> {
        val (query, hasDate) = getActivitiesQueryBuilder(date, rid, orderBy)
        val queryWithPaginationInfo = "$query LIMIT ? OFFSET ?"
        val pstmt = connection.prepareStatement(queryWithPaginationInfo)
        val ridIdx = if (hasDate) 3 else 2
        val pagIdx = if (rid != null) ridIdx + 1 else ridIdx
        pstmt.use { ps ->
            ps.apply {
                setInt(1, sid)
                date?.let {
                    setDate(2, Date.valueOf(it.toJavaLocalDate()))
                }
                rid?.let { rid ->
                    setInt(ridIdx, rid)
                }
                applyPagination(paginationInfo, Pair(pagIdx, pagIdx + 1))
            }
            val rs = ps.executeQuery()
            return rs.toListOf(ResultSet::toActivity)
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
    override fun deleteActivity(activityID: ActivityID): Boolean {
        val query = """DELETE FROM $activityTable WHERE id = ?"""
        connection.prepareStatement(query).use { ps ->
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
     * Gets the users that have an activity matching the given sport id and route id.
     * @param sportID sport identifier
     * @param routeID route identifier
     * @return [List] of [User]
     */
    override fun getUsersBy(sportID: SportID, routeID: RouteID, paginationInfo: PaginationInfo): List<User> {
        val query =
            "select distinct * from (" +
                "SELECT $userTable.id ,$userTable.name, $emailTable.email " +
                "FROM $activityTable " +
                "JOIN $userTable ON ($activityTable.user = $userTable.id) " +
                "JOIN $emailTable ON ($emailTable.user = $userTable.id) " +
                "WHERE $activityTable.sport = ? AND $activityTable.route = ? " +
                "ORDER BY $activityTable.duration DESC " +
                "LIMIT ? OFFSET ?) as t"
        connection.prepareStatement(query).use {
            it.applyPagination(paginationInfo, indexes = Pair(3, 4))
            it.setInt(1, sportID)
            it.setInt(2, routeID)
            val rs = it.executeQuery()
            return rs.toListOf(ResultSet::toUser)
        }
    }

    /**
     * Gets all existing activities.
     * @return [List] of [Activity]s
     */
    override fun getAllActivities(paginationInfo: PaginationInfo): List<Activity> {
        val query = """SELECT * FROM $activityTable LIMIT ? OFFSET ? """
        return connection.prepareStatement(query).use { ps ->
            ps.applyPagination(paginationInfo, Pair(1, 2))
            val rs: ResultSet = ps.executeQuery()
            rs.toListOf<Activity>(ResultSet::toActivity)
        }
    }

    /**
     * Deletes all the activities supplied in the list.
     * Atomic operation.
     * Either all activities are deleted or none.
     *
     * @param activities the list of activities to delete
     * @return [Boolean] true if it deleted successfully
     *
     */
    override fun deleteActivities(activities: List<ActivityID>): Boolean {
        val query = "DELETE FROM $activityTable WHERE id = ?"
        connection.prepareStatement(query).use<PreparedStatement, Boolean> { ps ->
            activities.forEach {
                ps.setInt(1, it)
                ps.addBatch()
            }
            return ps.executeBatch().all { it == 1 }
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
        connection.queryTableByID(activityID, activityTable, block)
}
