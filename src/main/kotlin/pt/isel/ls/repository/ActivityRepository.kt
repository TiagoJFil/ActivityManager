package pt.isel.ls.repository

import kotlinx.datetime.LocalDate
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Order
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo

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
     * Updates an existing activity using the parameters received
     *
     * @param newDate the activity date
     * @param newDuration the activity duration
     * @param newRouteID the activity route ID
     * @param activityID the activity ID
     *
     * @return [Boolean] indicating if the activity was updated or not
     */
    fun updateActivity(
        newDate: LocalDate?,
        newDuration: Activity.Duration?,
        newRouteID: RouteID?,
        activityID: ActivityID,
        removeRoute: Boolean
    ): Boolean

    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [ActivityDTO] that were created by the given user
     */
    fun getActivitiesByUser(userID: UserID, paginationInfo: PaginationInfo): List<Activity>

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
    fun getActivities(
        sid: SportID,
        orderBy: Order,
        date: LocalDate?,
        rid: RouteID?,
        paginationInfo: PaginationInfo
    ): List<Activity>

    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    fun deleteActivity(activityID: ActivityID): Boolean

    /**
     * Gets all existing activities.
     * @return [List] of [Activity]s
     */
    fun getAllActivities(paginationInfo: PaginationInfo): List<Activity>

    /**
     * Deletes all the activities supplied in the list.
     * Atomic operation.
     * Either all activities are deleted or none.
     *
     * @param activities the list of activities to delete
     * @return [Boolean] true if it deleted successfully
     *
     */
    fun deleteActivities(activities: List<ActivityID>): Boolean
}
