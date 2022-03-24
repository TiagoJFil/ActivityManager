package pt.isel.ls.repository

import pt.isel.ls.entities.*

typealias ActivityID = String

interface ActivityRepository {

    fun addActivity(activity: Activity)

    fun getActivitiesBySport(sportID: SportID): List<Activity>

    fun getActivitiesByUser(userID: UserID): List<Activity>

    fun getActivity(activityID: ActivityID): Activity?

    fun deleteActivity(activityID: ActivityID): Boolean

}