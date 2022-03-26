package pt.isel.ls.repository.memory

import kotlinx.datetime.LocalDate
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.*
import pt.isel.ls.utils.*



class ActivityDataMemRepository(): ActivityRepository {

    private val activitiesMap = mutableMapOf<ActivityID, Activity>()

    /**
     * Adds a new activity.
     *
     * @param activity the activity to be added
     */
    override fun addActivity(activity: Activity) {
        activitiesMap[activity.id] = activity
    }

    /**
     * Gets all the activities that contain the given sport id.
     *
     * @param sportID the sport unique identifier that the activity must contain
     * @return [List] of [Activity] that contain the given sport unique identifier
     */
    override fun getActivitiesBySport(sportID: SportID): List<Activity> {
        TODO("Not yet implemented")
    }

    /**
     * Gets all the activities that contain the given user id.
     *
     * @param userID the user unique identifier that the activity must contain
     * @return [List] of [Activity] that contain the given user unique identifier
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
     * @param orderBy order by duration time, this parameter only has two possible values - ascending or descending
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
    override fun deleteActivity(activityID: ActivityID): Boolean {
        TODO("Not yet implemented")
    }



}