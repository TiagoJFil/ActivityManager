package pt.isel.ls.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import pt.isel.ls.api.SportRoutes
import pt.isel.ls.services.dto.ActivityDTO
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Activity.Duration
import pt.isel.ls.utils.*
import java.util.*
import java.text.ParseException


class ActivityServices(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val sportRepository: SportRepository,
    private val routeRepository: RouteRepository
){
    companion object{
        val logger = getLoggerFor<ActivityServices>()
    }

    /**
     * Creates a new activity.
     *
     * @param token the token of the user that is going to create the activity
     * @param sportID the sports id of the activity
     * @param duration the duration of the activity
     * @param date the date of the activity
     * @param rid the id of the route where the activity will take place
     *
     * @return [ActivityID] the unique activity identifier
     */
    fun createActivity(token: UserToken?, sportID: String?, duration: String?, date: String?, rid: String?): ActivityID {
        try {
            logger.traceFunction("createActivity","sportID: $sportID, duration: $duration, date: $date, rid: $rid")
            val userID = userRepository.requireAuthenticated(token)
            val sid = requireParameter(sportID, "sportID")
            val safeDate = requireParameter(date, "date")
            requireParameter(duration, "duration")
            requireNotBlankParameter(rid, "rid")

            val parsedDate: Date = Duration.dateFormat.parse(duration)
            val millis: Long = parsedDate.time

            val activityID = generateRandomId()

            sportRepository.requireSport(sid)
            userRepository.requireUser(userID)
            if(rid != null) routeRepository.requireRoute(rid)

            activityRepository.addActivity(
                    activityID=activityID,
                    date=safeDate.toLocalDate(),
                    duration=Duration(millis),
                    sportID=sid,
                    routeID=rid,
                    userID=userID
            )

            return activityID
        }catch (e: ParseException){
            throw InvalidParameter("duration")
        }catch (e: IllegalArgumentException){
            throw InvalidParameter("date")
        }
    }

    /**
     * Gets an [ActivityDTO] by its id.
     *
     * @param activityID the unique identifier of the activity
     * @return [ActivityDTO] with the activity that matches the given id
     */
    fun getActivity(activityID: String?): ActivityDTO {
        logger.traceFunction("getActivity","activityID: $activityID")
        val safeActivityID = requireParameter(activityID, "activityID")

        return activityRepository.getActivity(safeActivityID)?.toDTO()
                ?: throw ResourceNotFound("Activity", safeActivityID)
    }

    /**
     * Gets the activities created by a user.
     *
     * @param userID the unique identifier of the user that created the activity
     * @return [List] of [ActivityDTO] with all the activities created by the user that matches the given id
     */
    fun getActivitiesByUser(userID: UserID?): List<ActivityDTO>{
        logger.traceFunction("getActivitiesByUser","userID: $userID")
        val safeUID = requireParameter(userID, "userID")

        userRepository.requireUser(safeUID)

        return activityRepository.getActivitiesByUser(safeUID)
                .map(Activity::toDTO)
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
     * @return [List] of [ActivityDTO]
     */
    fun getActivities(sid: SportID?, orderBy: String?, date: String?, rid: RouteID?): List<ActivityDTO>{
        logger.traceFunction("getActivities","sid: $sid","orderBy: $orderBy","date: $date","rid: $rid")
        val safeSID = requireParameter(sid, "sportID")
        sportRepository.requireSport(safeSID)

        val orderByToSend = when(orderBy?.lowercase()){
            "ascending", null -> Order.ASCENDING
            "descending" -> Order.DESCENDING
            else -> throw InvalidParameter("orderBy")
        }
        requireNotBlankParameter(date, "date")
        requireNotBlankParameter(rid, "rid")
        try{
            if(date != null) LocalDate.parse(date)
        }catch (e: IllegalArgumentException){
            throw InvalidParameter("date")
        }

        return activityRepository.getActivities(safeSID, orderByToSend, date?.toLocalDate(), rid)
                .map(Activity::toDTO)
    }

    /**
     * Deletes an activity.
     * @param token The token of the user that created the activity.
     * @param activityId The id of the activity to be deleted.
     * @param sportID The sport id of the activity to be deleted.
     * @return true if the activity was deleted, false otherwise.
     */
    fun deleteActivity(token: UserToken?, activityId: ActivityID?, sportID: SportID?): Boolean {
        logger.traceFunction("deleteActivity","activityId: $activityId","sportID: $sportID")
        val userID = userRepository.requireAuthenticated(token)
        val safeSID = requireParameter(sportID, "sportID")
        val safeAID = requireParameter(activityId, "activityId")
        sportRepository.requireSport(safeSID)
        activityRepository.requireActivity(safeAID)

        if(!ownsActivity(userID, safeAID)) throw UnauthenticatedError("User does not own this activity")

        return activityRepository.deleteActivity(safeAID)
    }

    /**
     * Checks if the user identified by [userId] owns the activity identified by [activityId].
     */
    private fun ownsActivity(userId: UserID, activityId: ActivityID): Boolean
        = activityRepository.getActivity(activityId)?.user == userId

}