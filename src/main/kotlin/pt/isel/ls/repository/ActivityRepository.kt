package pt.isel.ls.repository

import kotlinx.datetime.LocalDate
import pt.isel.ls.entities.Activity
import pt.isel.ls.utils.*


interface ActivityRepository {

    fun addActivity(activity: Activity)

    fun getActivitiesBySport(sportID: SportID): List<Activity>

    fun getActivitiesByUser(userID: UserID): List<Activity>

    fun getActivity(activityID: ActivityID): Activity?

    fun getActivities(sid: SportID, orderBy: Order, date: LocalDate?, rid: RouteID?): List<Activity>

    fun deleteActivity(activityID: ActivityID): Boolean

}
