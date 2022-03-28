package pt.isel.ls.repository

import kotlinx.datetime.LocalDate
import pt.isel.ls.entities.Activity
import pt.isel.ls.utils.*

interface ActivityRepository {

    /**
     * Adds a new activity.
     *
     * @param activity the activity to be added
     */
    fun addActivity(activity: Activity)

    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [Activity] that were created by the given user
     */
    fun getActivitiesByUser(userID: UserID): List<Activity>

    /**
     * Gets the activity that matches the given unique activity identifier.
     *
     * @param activityID the identifier of the activity to get
     * @return [Activity] if the id exists or null if it doesn't
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
     * @return [List] of [Activity]
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

}
