package pt.isel.ls.services


import kotlinx.datetime.toLocalDate
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.*
import java.time.DateTimeException
import java.time.format.DateTimeFormatter

class ActivityServices(val activityRepository: ActivityRepository, val userRepository: UserRepository){

    /**
     * Creates a new activity.
     *
     * @param userId the id of the user that created the activity
     * @param sportID the sports id of the activity
     * @param duration the duration of the activity
     * @param date the date of the activity
     * @param rid the id of the route where the activity will take place
     *
     * @return [ActivityID] the unique activity identifier
     */
    fun createActivity(userId: UserID, sportID: String?, duration: String?, date: String?, rid: String?): ActivityID {
        try {
            if(sportID == null) throw MissingParameter("sportsID")
            if(duration == null) throw MissingParameter("duration")
            if(date == null) throw MissingParameter("date")
            if(duration.isBlank()) throw InvalidParameter("duration")
            if(date.isBlank()) throw InvalidParameter("date")
            if(rid != null && rid.isBlank()) InvalidParameter("rid")
            val format = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            val formatVerification = format.parse(duration)

            val activityId = generateRandomId()
            val activity = Activity(activityId, date.toLocalDate(), duration, sportID, rid, userId)
            //TODO("SID_NOT_FOUND ERROR) and DATE_INVALID THROW

            activityRepository.addActivity(activity)
            return activityId
        }catch (e: DateTimeException){
            throw IllegalArgumentException("Wrong duration format")
        }
    }

    /**
     * Gets the activities created by a user.
     *
     * @param userID the unique identifier of the user that created the activity
     * @return [List] of [Activity] with all the activities created by the user that matches the given id
     */
    fun getActivitiesByUser(userID: UserID?): List<Activity>{
        if (userID == null) throw MissingParameter("userID")
        if (userID.isBlank()) throw InvalidParameter("userID")
        return if(userRepository.hasUser(userID)) activityRepository.getActivitiesByUser(userID)
        else throw ResourceNotFound("User", userID)
    }


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
    fun getActivities(sid: SportID?, orderBy: String?, date: String?, rid: RouteID?): List<Activity>{
        if (sid == null) throw MissingParameter("sid")
        if (sid.isBlank()) throw InvalidParameter("sid")

        val orderByToSend = when(orderBy?.lowercase()){
            "ascending", null -> Order.ASCENDING
            "descending" -> Order.DESCENDING
            else -> throw InvalidParameter("orderBy")
        }
        if(date != null && date.isBlank()) throw InvalidParameter("date")
        if(rid != null && rid.isBlank()) throw InvalidParameter("rid")


        return activityRepository.getActivities(sid, orderByToSend, date?.toLocalDate(), rid)
    }

}