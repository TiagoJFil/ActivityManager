package pt.isel.ls.service

import pt.isel.ls.service.SportsServices.Companion.SPORT_ID_PARAM
import pt.isel.ls.service.UserServices.Companion.USER_ID_PARAM
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Order
import pt.isel.ls.service.inputs.ActivityInputs.ActivityCreateInput
import pt.isel.ls.service.inputs.ActivityInputs.ActivitySearchInput
import pt.isel.ls.service.inputs.ActivityInputs.ActivityUpdateInput
import pt.isel.ls.service.inputs.ActivityInputs.validateID
import pt.isel.ls.service.transactions.TransactionFactory
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.loggerFor
import pt.isel.ls.utils.service.requireActivityWith
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireOwnership
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.requireRoute
import pt.isel.ls.utils.service.requireSport
import pt.isel.ls.utils.service.requireUser
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction

class ActivityServices(
    private val transactionFactory: TransactionFactory
) {
    companion object {
        private val logger = loggerFor<ActivityServices>()

        const val ACTIVITY_ID_PARAM = "activityID"
        const val ROUTE_ID_PARAM = "routeID"
        const val ACTIVITIES_ID_PARAM = "activityIDs"
        const val RESOURCE_NAME = "Activity"
        const val DURATION_PARAM = "duration"
        const val DATE_PARAM = "date"
        const val ORDER_PARAM = "orderBy"
        val ORDER_POSSIBLE_VALUES = "$ORDER_PARAM should be one of: ${Order.values().joinToString(", ")}"
        const val DATE_INVALID_FORMAT = "Date should have format yyyy-MM-dd"
        const val DURATION_INVALID_FORMAT = "Duration should have format hh:mm:ss.fff"
    }

    /**
     * Creates a new activity.
     *
     * @param token the token of the user that is going to create the activity
     * @param activityCreateInput [ActivityCreateInput] data given by the user creating the activity,
     *
     * @return [ActivityID] the unique activity identifier
     */
    fun createActivity(
        token: UserToken?,
        activityCreateInput: ActivityCreateInput
    ): ActivityID {

        val sportID = activityCreateInput.sportID
        val routeID = activityCreateInput.routeID
        val duration = activityCreateInput.activityDuration
        val date = activityCreateInput.activityDate

        logger.traceFunction(::createActivity.name) {
            listOf(
                SPORT_ID_PARAM to sportID,
                DURATION_PARAM to routeID,
                DATE_PARAM to duration,
                ROUTE_ID_PARAM to date
            )
        }

        return transactionFactory.getTransaction().execute {

            val userID = usersRepository.requireAuthenticated(token)
            usersRepository.requireUser(userID)
            sportsRepository.requireSport(activityCreateInput.sportID)
            routeID?.let { routesRepository.requireRoute(it) }

            activitiesRepository.addActivity(
                date = date,
                duration = duration,
                sportID = sportID,
                routeID = routeID,
                userID = userID
            )
        }
    }

    /**
     * Gets an [ActivityDTO] by its id.
     *
     * @param activityID the unique identifier of the activity
     * @return [ActivityDTO] with the activity that matches the given id
     */
    fun getActivity(activityID: Param, sid: Param): ActivityDTO {
        logger.traceFunction(::getActivity.name) { listOf(ACTIVITY_ID_PARAM to activityID) }
        val aidInt = validateID(activityID, ACTIVITY_ID_PARAM)
        val sidInt = validateID(sid, SPORT_ID_PARAM)
        return transactionFactory.getTransaction().execute {
            sportsRepository.requireSport(sidInt)
            activitiesRepository.requireActivityWith(aidInt, sidInt)

            activitiesRepository.getActivity(aidInt)?.toDTO()
                ?: throw ResourceNotFound(RESOURCE_NAME, sidInt.toString())
        }
    }

    /**
     * Gets the activities created by a user.
     *
     * @param uid the unique identifier of the user that created the activity
     * @return [List] of [ActivityDTO] with all the activities created by the user that matches the given id
     */
    fun getActivitiesByUser(uid: Param, paginationInfo: PaginationInfo = PaginationInfo(10, 0)): List<ActivityDTO> {
        logger.traceFunction(::getActivitiesByUser.name) { listOf("userID" to uid) }

        val uidInt = validateID(uid, USER_ID_PARAM)

        return transactionFactory.getTransaction().execute {
            usersRepository.requireUser(uidInt)

            activitiesRepository.getActivitiesByUser(uidInt, paginationInfo)
                .map(Activity::toDTO)
        }
    }

    /**
     * Gets the activities that match the given sport id, date, route id
     * and orders it by the given orderBy ([Order.ASCENDING] or [Order.DESCENDING])
     *
     * @param activity [ActivitySearchInput] data given by the user searching for the activity.
     *
     * @return [List] of [ActivityDTO]
     */
    fun getActivities(
        activity: ActivitySearchInput,
        paginationInfo: PaginationInfo
    ): List<ActivityDTO> {

        val sportID = activity.sportID
        val routeID = activity.routeID
        val orderBy = activity.order
        val date = activity.activityDate

        logger.traceFunction(::getActivities.name) {
            listOf(
                SPORT_ID_PARAM to sportID,
                ORDER_PARAM to orderBy,
                DATE_PARAM to date,
                ROUTE_ID_PARAM to routeID
            )
        }

        return transactionFactory.getTransaction().execute {

            sportsRepository.requireSport(sportID)

            activitiesRepository.getActivities(sportID, orderBy, date, routeID, paginationInfo)
                .map(Activity::toDTO)
        }
    }

    /**
     * Updates an activity with the new given data.
     * @param token the token of the user that wants to update the activity
     * @param activity [ActivityUpdateInput] data given by the user updating the activity.
     */
    fun updateActivity(token: UserToken?, activity: ActivityUpdateInput) {

        val sportID = activity.sportID
        val routeID = activity.routeID
        val activityID = activity.activityID
        val date = activity.activityDate
        val duration = activity.activityDuration

        logger.traceFunction(::updateActivity.name) {
            listOf(
                DURATION_PARAM to duration,
                DATE_PARAM to date,
                ROUTE_ID_PARAM to routeID
            )
        }
        return transactionFactory.getTransaction().execute {

            val userID = usersRepository.requireAuthenticated(token)
            usersRepository.requireUser(userID)
            activitiesRepository.requireOwnership(userID, activityID)
            sportID?.let {
                sportsRepository.requireSport(it)
                activitiesRepository.requireActivityWith(activityID, it)
            }
            routeID?.let { routesRepository.requireRoute(it) }

            if (activity.hasNothingToUpdate) return@execute

            if (!activitiesRepository.updateActivity(
                    newDate = date,
                    newDuration = duration,
                    newRouteID = routeID,
                    activityID = activityID,
                    activity.removeRoute
                )
            ) throw ResourceNotFound(SportsServices.RESOURCE_NAME, activityID.toString())
        }
    }

    /**
     * Deletes an activity.
     * @param token The token of the user that created the activity.
     * @param aid The id of the activity to be deleted.
     * @param sid The sport id of the activity to be deleted.
     * @return true if the activity was deleted, false otherwise.
     */
    fun deleteActivity(token: UserToken?, aid: Param, sid: Param) {
        logger.traceFunction(::deleteActivity.name) {
            listOf(
                ACTIVITY_ID_PARAM to aid,
                SPORT_ID_PARAM to sid
            )
        }
        return transactionFactory.getTransaction().execute {
            val userID = usersRepository.requireAuthenticated(token)

            val sportID = validateID(sid, SPORT_ID_PARAM)
            val activityID = validateID(aid, ACTIVITY_ID_PARAM)

            activitiesRepository.requireOwnership(userID, activityID)
            sportsRepository.requireSport(sportID)
            activitiesRepository.requireActivityWith(activityID, sportID)
            activitiesRepository.deleteActivity(activityID)
        }
    }

    /**
     * Deletes all activities received.
     * This operation is atomic
     * @param token The token of the user that created the activities.
     * @param activityIds The activityIds to be deleted.
     */
    fun deleteActivities(token: UserToken?, activityIds: Param): Boolean {
        logger.traceFunction(::deleteActivities.name) {
            listOf(
                ACTIVITIES_ID_PARAM to activityIds
            )
        }

        return transactionFactory.getTransaction().execute {
            val userID = usersRepository.requireAuthenticated(token)
            val safeAIDS = requireParameter(activityIds, "activityIDs")

            val checkedAIDS = safeAIDS.split(",").map {
                val aidInt = requireIdInteger(it, "Each activityID")
                activitiesRepository.requireOwnership(userID, aidInt)
                aidInt
            }
            if (!activitiesRepository.deleteActivities(checkedAIDS))
                throw InvalidParameter("Failed to delete activities.")
            true
        }
    }

    /**
     * Gets all existing activities.
     * @return A [List] of all existing [Activity]s.
     */
    fun getAllActivities(paginationInfo: PaginationInfo): List<ActivityDTO> {
        logger.traceFunction(::getAllActivities.name) {
            listOf(
                "paginationInfo" to paginationInfo.toString()
            )
        }
        return transactionFactory.getTransaction().execute {
            activitiesRepository.getAllActivities(paginationInfo).map(Activity::toDTO)
        }
    }
}
