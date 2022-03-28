package pt.isel.ls.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.*
import java.time.DateTimeException
import java.time.format.DateTimeFormatter

class ActivityServices(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val sportRepository: SportRepository
){

    private val durationFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
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
            val sid = requireParameter(sportID, "sportID")
            val safeDuration = requireParameter(duration, "duration")
            val safeDate = requireParameter(date, "date")
            if(rid != null && rid.isBlank()) throw InvalidParameter("rid")
            durationFormat.parse(duration)
            val activityID = generateRandomId()
            val activity = Activity(
                id=activityID,
                date=safeDate.toLocalDate(),
                duration=safeDuration,
                sport=sid,
                route=rid,
                user=userId
            )

            activityRepository.addActivity(activity)

            return activityID

        }catch (e: DateTimeException){
            throw InvalidParameter("duration")
        }catch (e: IllegalArgumentException){
            throw InvalidParameter("date")
        }
    }

    /**
     * Gets the activities created by a user.
     *
     * @param userID the unique identifier of the user that created the activity
     * @return [List] of [Activity] with all the activities created by the user that matches the given id
     */
    fun getActivitiesByUser(userID: UserID?): List<Activity>{

        val safeUID = requireParameter(userID, "userID")
        if(!userRepository.hasUser(safeUID)) throw ResourceNotFound("userID", safeUID)

        return activityRepository.getActivitiesByUser(safeUID)

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
        val safeSID = requireParameter(sid, "sportID")

        val orderByToSend = when(orderBy?.lowercase()){
            "ascending", null -> Order.ASCENDING
            "descending" -> Order.DESCENDING
            else -> throw InvalidParameter("orderBy")
        }
        if(date != null && date.isBlank()) throw InvalidParameter("date")
        if(rid != null && rid.isBlank()) throw InvalidParameter("rid")

        try{
            if(date != null) LocalDate.parse(date)
        }catch (e: IllegalArgumentException){
            throw InvalidParameter("date")
        }

        return activityRepository.getActivities(safeSID, orderByToSend, date?.toLocalDate(), rid)
    }

    /**
     * Deletes an activity.
     * @param activityId The id of the activity to be deleted.
     * @param userID The id of the user that created the activity.
     * @return true if the activity was deleted, false otherwise.
     */
    fun deleteActivity(userID: UserID, activityId: ActivityID?, sportID: SportID?): Boolean {

        val safeSID = requireParameter(sportID, "sportID")
        if(!sportRepository.hasSport(safeSID)) throw ResourceNotFound("Sport", safeSID)
        val safeAID = requireParameter(activityId, "activityId")
        if(!activityRepository.hasActivity(safeAID)) throw ResourceNotFound("activityId", safeAID)
        if(!ownsActivity(userID, safeAID)) throw UnauthenticatedError("User does not own this activity")

        return activityRepository.deleteActivity(safeAID)
    }

    /**
     * Checks if the user identified by [userId] owns the activity identified by [activityId].
     */
    private fun ownsActivity(userId: UserID, activityId: ActivityID): Boolean
        = activityRepository.getActivity(activityId)?.user == userId


}