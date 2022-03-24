package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Activity
import pt.isel.ls.entities.SportID
import pt.isel.ls.entities.User
import pt.isel.ls.repository.*


class ActivityDataMemRepository(): ActivityRepository {

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

    override fun deleteActivity(activityID: ActivityID): Boolean {
        TODO("Not yet implemented")
    }

}