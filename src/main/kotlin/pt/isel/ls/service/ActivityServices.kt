package pt.isel.ls.service

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.RouteServices.Companion.ROUTE_ID_PARAM
import pt.isel.ls.service.SportsServices.Companion.SPORT_ID_PARAM
import pt.isel.ls.service.UserServices.Companion.USER_ID_PARAM
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.Order
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.service.requireActivity
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireNotBlankParameter
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.requireRoute
import pt.isel.ls.utils.service.requireSport
import pt.isel.ls.utils.service.requireUser
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction
import java.text.ParseException
import java.util.Date

class ActivityServices(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val sportRepository: SportRepository,
    private val routeRepository: RouteRepository
) {
    companion object {
        val logger = getLoggerFor<ActivityServices>()

        const val USER_NOT_OWNER = "User is not the owner of the activity"
        const val ACTIVITY_ID_PARAM = "activityID"
        const val RESOURCE_NAME = "Activity"
        const val DURATION_PARAM = "duration"
        const val DATE_PARAM = "date"
        const val ORDER_PARAM = "orderBy"
        val ORDER_POSSIBLE_VALUES = "$ORDER_PARAM should be one of: ${Order.values().joinToString(", ")}"
        const val DATE_INVALID_FORMAT = "Date should have format yyyy-MM-dd"
        const val DURATION_INVALID_FORMAT = "Duration should have format hh:mm:ss.ffff"
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
    fun createActivity(
        token: UserToken?,
        sportID: Param,
        duration: Param,
        date: Param,
        rid: Param
    ): ActivityID {
        try {
            logger.traceFunction(::createActivity.name) {
                listOf(
                    SPORT_ID_PARAM to sportID,
                    DURATION_PARAM to duration,
                    DATE_PARAM to date,
                    ROUTE_ID_PARAM to rid
                )
            }

            val userID = userRepository.requireAuthenticated(token)
            val sid = requireParameter(sportID, SPORT_ID_PARAM)
            val safeDate = requireParameter(date, DATE_PARAM)
            requireParameter(duration, DURATION_PARAM)
            requireNotBlankParameter(rid, ROUTE_ID_PARAM)

            val parsedDate: Date = Duration.format.parse(duration)
            val millis: Long = parsedDate.time
            val sidInt = requireIdInteger(sid, SPORT_ID_PARAM)

            sportRepository.requireSport(sidInt)
            userRepository.requireUser(userID)
            val ridInt = rid?.let {
                val ridInt = requireIdInteger(it, ROUTE_ID_PARAM)
                routeRepository.requireRoute(ridInt)
                ridInt
            }
            return activityRepository.addActivity(
                date = safeDate.toLocalDate(),
                duration = Duration(millis),
                sportID = sidInt,
                routeID = ridInt,
                userID = userID
            )
        } catch (e: ParseException) {
            throw InvalidParameter(DURATION_INVALID_FORMAT)
        } catch (e: IllegalArgumentException) {
            throw InvalidParameter(DATE_INVALID_FORMAT)
        }
    }

    /**
     * Gets an [ActivityDTO] by its id.
     *
     * @param activityID the unique identifier of the activity
     * @return [ActivityDTO] with the activity that matches the given id
     */
    fun getActivity(activityID: Param): ActivityDTO {
        logger.traceFunction(::getActivity.name) { listOf(ACTIVITY_ID_PARAM to activityID) }
        val safeActivityID = requireParameter(activityID, ACTIVITY_ID_PARAM)
        val aidInt = requireIdInteger(safeActivityID, ACTIVITY_ID_PARAM)

        return activityRepository.getActivity(aidInt)?.toDTO()
            ?: throw ResourceNotFound(RESOURCE_NAME, safeActivityID)
    }

    /**
     * Gets the activities created by a user.
     *
     * @param uid the unique identifier of the user that created the activity
     * @return [List] of [ActivityDTO] with all the activities created by the user that matches the given id
     */
    fun getActivitiesByUser(uid: Param): List<ActivityDTO> {
        logger.traceFunction(::getActivitiesByUser.name) { listOf("userID" to uid) }
        val safeUID = requireParameter(uid, USER_ID_PARAM)

        val uidInt = requireIdInteger(safeUID, USER_ID_PARAM)
        userRepository.requireUser(uidInt)

        return activityRepository.getActivitiesByUser(uidInt)
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
    fun getActivities(sid: Param, orderBy: Param, date: Param, rid: Param): List<ActivityDTO> {
        logger.traceFunction(::getActivities.name) {
            listOf(
                SPORT_ID_PARAM to sid,
                ORDER_PARAM to orderBy,
                DATE_PARAM to date,
                ROUTE_ID_PARAM to rid
            )
        }
        val safeSID = requireParameter(sid, SPORT_ID_PARAM)
        val sidInt: SportID = requireIdInteger(safeSID, SPORT_ID_PARAM)
        sportRepository.requireSport(sidInt)

        val orderByToSend = when (orderBy?.lowercase()) {
            "ascending", null -> Order.ASCENDING
            "descending" -> Order.DESCENDING
            else -> throw InvalidParameter(ORDER_POSSIBLE_VALUES)
        }

        requireNotBlankParameter(date, DATE_PARAM)
        requireNotBlankParameter(rid, ROUTE_ID_PARAM)
        val ridInt: RouteID? = rid?.let { requireIdInteger(it, ROUTE_ID_PARAM) }
        ridInt?.let { routeRepository.requireRoute(ridInt) }

        try {
            if (date != null) LocalDate.parse(date)
        } catch (e: IllegalArgumentException) {
            throw InvalidParameter(DATE_PARAM)
        }

        return activityRepository.getActivities(sidInt, orderByToSend, date?.toLocalDate(), ridInt)
            .map(Activity::toDTO)
    }

    /**
     * Deletes an activity.
     * @param token The token of the user that created the activity.
     * @param aid The id of the activity to be deleted.
     * @param sid The sport id of the activity to be deleted.
     * @return true if the activity was deleted, false otherwise.
     */
    fun deleteActivity(token: UserToken?, aid: Param, sid: Param): Boolean {
        logger.traceFunction(::deleteActivity.name) {
            listOf(
                ACTIVITY_ID_PARAM to aid,
                SPORT_ID_PARAM to sid
            )
        }
        val userID = userRepository.requireAuthenticated(token)
        val safeSID = requireParameter(sid, SPORT_ID_PARAM)
        val safeAID = requireParameter(aid, ACTIVITY_ID_PARAM)
        val sidInt = requireIdInteger(safeSID, SPORT_ID_PARAM)
        sportRepository.requireSport(sidInt)
        val aidInt = requireIdInteger(safeAID, ACTIVITY_ID_PARAM)
        activityRepository.requireActivity(aidInt)

        if (!ownsActivity(userID, aidInt)) throw UnauthenticatedError(USER_NOT_OWNER)

        return activityRepository.deleteActivity(aidInt)
    }

    /**
     * Checks if the user identified by [userId] owns the activity identified by [activityId].
     */
    private fun ownsActivity(userId: UserID, activityId: ActivityID): Boolean =
        activityRepository.getActivity(activityId)?.user == userId
}
