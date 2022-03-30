package pt.isel.ls.repository.memory

import kotlinx.datetime.LocalDate
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.repository.*
import pt.isel.ls.utils.*


class ActivityDataMemRepository(testActivity: Activity): ActivityRepository {

    /**
     * Mapping between the [ActivityID] and the [Activity]
     */
    private val activitiesMap = mutableMapOf<ActivityID, Activity>(testActivity.id to testActivity)

    /**
     * Creates a new activity using the parameters received
     *
     * @param activityID the activity ID
     * @param date the activity date
     * @param duration the activity duration
     * @param sportID the activity sport ID
     * @param routeID the activity route ID
     * @param userID the activity user ID
     */
    override fun addActivity(
            activityID: ActivityID,
            date: LocalDate,
            duration: Activity.Duration,
            sportID: SportID,
            routeID: RouteID?,
            userID: UserID
    ) {
        val activity = Activity(activityID, date, duration, sportID,routeID, userID)
        activitiesMap[activity.id] = activity
    }

    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [Activity] that were created by the given user
     */
    override fun getActivitiesByUser(userID: UserID): List<Activity> = activitiesMap.values.filter { it.user == userID }

    /**
     * Gets the activity that matches the given unique activity identifier.
     *
     * @param activityID the identifier of the activity to get
     * @return [Activity] if the id exists or null if it doesn't
     */
    override fun getActivity(activityID: ActivityID): Activity? = activitiesMap[activityID]

    /**
     * Gets the activities that match the given sport id, date, route id
     * and orders it by the given orderBy ([Order.ASCENDING] or [Order.DESCENDING])
     *
     * @param sid sport identifier
     * @param orderBy order by duration time, this parameter only has two possible values - [Order.ASCENDING] or [Order.DESCENDING]
     * @param date activity date (optional)
     * @param rid route identifier (optional)
     *
     * @return [List] of [Activity]
     */
    override fun getActivities(sid: SportID, orderBy: Order, date: LocalDate?, rid: RouteID?): List<Activity> {

        val activities = activitiesMap.values.filter {
            when {
                date == null && rid == null -> it.sport == sid
                rid == null -> it.sport == sid && it.date == date
                date == null -> it.sport == sid && it.route == rid
                else -> it.sport == sid && it.date == date && it.route==rid
            }
        }

        if (activities.isEmpty()) return activities

        return if (orderBy == Order.ASCENDING)
            activities.sortedBy { it.duration }
        else
            activities.sortedByDescending { it.duration }
    }

    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    override fun deleteActivity(activityID: ActivityID): Boolean
            = activitiesMap.remove(activityID) != null

    /**
     * Checks if the activity identified by the given identifier exists.
     * @param activityID the id of the activity to check
     * @return [Boolean] true if it exists
     */
    override fun hasActivity(activityID: ActivityID): Boolean = activitiesMap.containsKey(activityID)

}