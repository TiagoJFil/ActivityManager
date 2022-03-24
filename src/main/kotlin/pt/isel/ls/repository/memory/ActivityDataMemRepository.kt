package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Activity


import pt.isel.ls.repository.*
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID


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