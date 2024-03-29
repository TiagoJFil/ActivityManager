package pt.isel.ls.repository.memory

import kotlinx.datetime.LocalDate
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Order
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.applyPagination

class ActivityDataMemRepository(testActivity: Activity) : ActivityRepository {

    private var currentID = 0

    /**
     * Mapping between the [ActivityID] and the [Activity]
     */
    private val activitiesMap = mutableMapOf(testActivity.id to testActivity)

    val map: Map<Int, Activity>
        get() = activitiesMap.toMap()

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
    ): ActivityID {
        val activityID = ++currentID
        val activity = Activity(activityID, date, duration, sportID, routeID, userID)
        activitiesMap[activity.id] = activity
        return activityID
    }

    /**
     * Updates an activity.
     *
     * @param newDate the activity date
     * @param newDuration the activity duration
     * @param newRouteID the activity route ID
     * @param activityID the activity ID
     */
    override fun updateActivity(
        newDate: LocalDate?,
        newDuration: Activity.Duration?,
        newRouteID: RouteID?,
        activityID: ActivityID,
        removeRoute: Boolean
    ): Boolean {
        val activity = activitiesMap[activityID] ?: return false
        val duration = newDuration ?: activity.duration
        val date = newDate ?: activity.date
        val route = if (removeRoute)
            null
        else
            newRouteID ?: activity.route
        activitiesMap[activityID] = activity.copy(date = date, duration = duration, route = route)
        return true
    }

    /**
     * Gets all the activities that were created by the given user.
     * @param userID the user unique identifier that the activity must have
     * @return [List] of [Activity] that were created by the given user
     */
    override fun getActivitiesByUser(userID: UserID, paginationInfo: PaginationInfo): List<Activity> =
        activitiesMap.values.filter { it.user == userID }.applyPagination(paginationInfo)

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
    override fun getActivities(sid: SportID, orderBy: Order, date: LocalDate?, rid: RouteID?, paginationInfo: PaginationInfo): List<Activity> {

        val activities = activitiesMap.values.filter {
            when {
                date == null && rid == null -> it.sport == sid
                rid == null -> it.sport == sid && it.date == date
                date == null -> it.sport == sid && it.route == rid
                else -> it.sport == sid && it.date == date && it.route == rid
            }
        }

        if (activities.isEmpty()) return activities

        val activitiesList = if (orderBy == Order.ASCENDING)
            activities.sortedBy { it.duration.millis }
        else
            activities.sortedByDescending { it.duration.millis }
        return activitiesList.applyPagination(paginationInfo)
    }

    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    override fun deleteActivity(activityID: ActivityID): Boolean =
        activitiesMap.remove(activityID) != null

    /**
     * Gets all existing activities.
     * @return [List] of [Activity]s
     */
    override fun getAllActivities(paginationInfo: PaginationInfo): List<Activity> =
        activitiesMap.values.toList().applyPagination(paginationInfo)

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
        val activitiesObjects = activities.toSet().filter { activitiesMap[it] != null }
        val entries = activitiesMap.filter { it.key in activitiesObjects }
        if (activitiesObjects.size != activities.size) return false
        for (it in activities) {
            activitiesMap.remove(it) // If could not remove add all again
                ?: run {
                    activitiesMap.putAll(entries)
                    return false
                }
        }
        return true
    }
}
