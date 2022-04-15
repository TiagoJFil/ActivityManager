package pt.isel.ls.repository

import kotlinx.datetime.LocalDate
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.User
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.Order
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

interface ActivityRepository {

    /**
     * Creates a new activity using the parameters received
     *
     * @param date the activity date
     * @param duration the activity duration
     * @param sportID the activity sport ID
     * @param routeID the activity route ID
     * @param userID the activity user ID
     */
    fun addActivity(
        date: LocalDate,
        duration: Activity.Duration,
        sportID: SportID,
        routeID: RouteID?,
        userID: UserID
    ): ActivityID

    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [ActivityDTO] that were created by the given user
     */
    fun getActivitiesByUser(userID: UserID): List<Activity>

    /**
     * Gets the activity that matches the given unique activity identifier.
     *
     * @param activityID the identifier of the activity to get
     * @return [ActivityDTO] if the id exists or null if it doesn't
     */
    fun getActivity(activityID: ActivityID): Activity?

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
     * @return [List] of [ActivityDTO]
     */
    fun getActivities(sid: SportID, orderBy: Order, date: LocalDate?, rid: RouteID?): List<Activity>

    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    fun deleteActivity(activityID: ActivityID): Boolean

    /**
     * Checks if the activity identified by the given identifier exists.
     * @param activityID the id of the activity to check
     * @return [Boolean] true if it exists
     */
    fun hasActivity(activityID: ActivityID): Boolean

    /**
     * Gets the users that have an activity matching the given sport id and route id.
     * @param sportID sport identifier
     * @param routeID route identifier
     * @return [List] of [User]
     */
    fun getUsersBy(sportID: SportID, routeID: RouteID): List<User>
}
