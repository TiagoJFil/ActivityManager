package pt.isel.ls.repository.db

import kotlinx.datetime.LocalDate
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.utils.*


class ActivityDbRepository: ActivityRepository {

    override fun addActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun getActivitiesBySport(sportID: SportID): List<Activity> {
        TODO("Not yet implemented")
    }

    override fun getActivitiesByUser(userID: UserID): List<Activity> {
        TODO("Not yet implemented")
    }

    override fun getActivity(activityID: ActivityID): Activity? {
        TODO("Not yet implemented")
    }

    override fun getActivities(sid: SportID, orderBy: Order, date: LocalDate?, rid: RouteID?): List<Activity> {
        TODO("Not yet implemented")
    }

    override fun deleteActivity(activityID: ActivityID): Boolean {
        TODO("Not yet implemented")
    }
}