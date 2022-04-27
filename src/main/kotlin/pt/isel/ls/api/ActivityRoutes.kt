package pt.isel.ls.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.service.ActivityServices
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.infoLogRequest

class ActivityRoutes(
    private val activityServices: ActivityServices
) {
    @Serializable data class ActivityCreationInput(
        val duration: Param = null,
        val date: Param = null,
        val rid: Param = null,
    )
    @Serializable data class ActivityIDOutput(val activityID: ActivityID)
    @Serializable data class ActivityListOutput(val activities: List<ActivityDTO>)
    companion object {
        val logger = getLoggerFor<ActivityRoutes>()
    }

    /**
     * Creates an [ActivityDTO] using the information received in the path and body of the request.
     */
    private fun createActivity(request: Request): Response {
        logger.infoLogRequest(request)

        val sportID = request.path("sid")

        val activityBody = Json.decodeFromString<ActivityCreationInput>(request.bodyString())
        val token: UserToken? = getToken(request)

        val activityId = activityServices.createActivity(
            token,
            sportID,
            activityBody.duration,
            activityBody.date,
            activityBody.rid
        )
        return Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(ActivityIDOutput(activityId)))
    }

    /**
     * Gets the [ActivityDTO] with the given [ActivityID].
     */
    private fun getActivity(request: Request): Response {
        logger.infoLogRequest(request)

        val activityId = request.path("aid")
        val sportId = request.path("sid")

        val activity = activityServices.getActivity(activityId,sportId)

        val activityJson = Json.encodeToString<ActivityDTO>(activity)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(activityJson)
    }

    /**
     * Gets all the activities created by the user that matches the given id.
     */
    private fun getActivitiesByUser(request: Request): Response {
        logger.infoLogRequest(request)

        val userId = request.path("uid")

        val activities = activityServices.getActivitiesByUser(userId, PaginationInfo.fromRequest(request))
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(activitiesJson)
    }

    /**
     * Gets all existing activities.
     */
    private fun getAllActivities(request: Request) : Response {
        logger.infoLogRequest(request)
        val activities = activityServices.getAllActivities(PaginationInfo.fromRequest(request))
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(activitiesJson)
    }

    /**
     * Handler for deleting an [ActivityDTO] using the information received in the path of the request.
     */
    private fun deleteActivity(request: Request): Response {
        logger.infoLogRequest(request)

        val activityId = request.path("aid")
        val sportID = request.path("sid")

        val token: UserToken? = getToken(request)

        activityServices.deleteActivity(token, activityId, sportID)
        return Response(Status.NO_CONTENT)
    }

    /**
     * Handler for deleting [ActivityDTO]s using the information received in the path of the request.
     */
    private fun deleteActivities(request: Request): Response {
        logger.infoLogRequest(request)

        val activityIds = request.path("activityIDs")
        val sportID = request.path("sid")
        //val body = Json.decodeFromString<ActivitiesInput>(request.bodyString())
        val token: UserToken? = getToken(request)

        activityServices.deleteActivities(token, activityIds,sportID)
        return Response(Status.NO_CONTENT)
    }

    /**
     * Get the list of [User] that have an activity with the given sport and rid.
     */
    private fun getUsersByActivity(request: Request): Response {
        logger.infoLogRequest(request)
        val sportID = request.path("sid")
        val routeID = request.query("rid")

        val users = activityServices.getUsersByActivity(sportID, routeID, PaginationInfo.fromRequest(request))

        val bodyString = Json.encodeToString(users)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(bodyString)
    }

    /**
     * Gets all the activities of the sport identified by the id given in the params of the uri's path.
     */
    private fun getActivitiesBySport(request: Request): Response {
        logger.infoLogRequest(request)

        val order = request.query("orderBy")
        val date = request.query("date")
        val routeID = request.query("rid")
        val sportID = request.path("sid")

        val activities = activityServices.getActivities(
            sportID,
            order,
            date,
            routeID,
            PaginationInfo.fromRequest(request)
        )
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(activitiesJson)
    }

    val handler = routes(
        "/sports/{sid}/activities" bind routes(
            "/" bind Method.POST to ::createActivity,
            "/{aid}" bind Method.DELETE to ::deleteActivity,
            "/{aid}" bind Method.GET to ::getActivity,
            "/" bind Method.GET to ::getActivitiesBySport,
            "/" bind Method.DELETE to ::deleteActivities
        ),
        "/users/{uid}/activities" bind Method.GET to ::getActivitiesByUser,
        "/sports/{sid}/users" bind Method.GET to ::getUsersByActivity,
        "/activities" bind Method.GET to ::getAllActivities
    )
}

fun Activity(activityServices: ActivityServices) =
    ActivityRoutes(activityServices).handler
