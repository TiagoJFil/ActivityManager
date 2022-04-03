package pt.isel.ls.repository.database

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.database.utils.generatedKey
import pt.isel.ls.repository.database.utils.transaction
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.utils.*
import java.sql.Date
import java.sql.Statement
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
        duration: Activity.Duration,
        sportID: SportID,
        routeID: RouteID?,
        userID: UserID
    ): ActivityID
        = dataSource.connection.transaction {
            val query = """INSERT INTO activity (date, duration, sport, route, "user")VALUES (?, ?, ?, ?, ?)"""
            val pstmt =  prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            pstmt.use { ps ->
                ps.apply {
                    setDate(1, Date.valueOf(date.toJavaLocalDate()))
                    setLong(2, duration.millis)
                    setInt(3, sportID.toInt())
                    routeID?.let {
                        setInt(4, it.toInt())
                    } ?: setNull(4, INTEGER)
                    setInt(5, userID.toInt())
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
        TODO("Not yet implemented")
    }

    /**
     * Gets the activity that matches the given unique activity identifier.
     *
     * @param activityID the identifier of the activity to get
     * @return [Activity] if the id exists or null if it doesn't
     */
    override fun getActivity(activityID: ActivityID): Activity? {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    override fun deleteActivity(activityID: ActivityID): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the activity identified by the given identifier exists.
     * @param activityID the id of the activity to check
     * @return [Boolean] true if it exists
     */
    override fun hasActivity(activityID: ActivityID): Boolean {
        TODO("Not yet implemented")
    }

}