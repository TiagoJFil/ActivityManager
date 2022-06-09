package pt.isel.ls.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.api.UserRoutes.UserListOutput
import pt.isel.ls.service.ActivityServices
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.api.contentJson
import pt.isel.ls.utils.api.pagination
import pt.isel.ls.utils.api.token
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.infoLogRequest

class ActivityRoutes(
    private val activityServices: ActivityServices
) {
    @Serializable data class ActivityInput(
        val duration: Param = null,
        val date: Param = null,
        val rid: Param = null,
    )
    @Serializable data class ActivityIDOutput(val activityID: ActivityID)
    @Serializable data class ActivityListOutput(val activities: List<ActivityDTO>)
    companion object {
        private val logger = getLoggerFor<ActivityRoutes>()
    }

    /**
     * Creates an [ActivityDTO] using the information received in the path and body of the request.
     */
    private fun createActivity(request: Request): Response {
        logger.infoLogRequest(request)

        val sportID = request.path("sid")

        val activityBody = Json.decodeFromString<ActivityInput>(request.bodyString())

        val activityId = activityServices.createActivity(
            request.token,
            sportID,
            activityBody.duration,
            activityBody.date,
            activityBody.rid
        )
        return Response(Status.CREATED)
            .contentJson()
            .body(Json.encodeToString(ActivityIDOutput(activityId)))
    }

    /**
     * Gets the [ActivityDTO] with the given [ActivityID].
     */
    private fun getActivity(request: Request): Response {
        logger.infoLogRequest(request)

        val activityId = request.path("aid")
        val sportId = request.path("sid")

        val activity = activityServices.getActivity(activityId, sportId)

        val activityJson = Json.encodeToString<ActivityDTO>(activity)

        return Response(Status.OK)
            .contentJson()
            .body(activityJson)
    }

    /**
     * Gets all the activities created by the user that matches the given id.
     */
    private fun getActivitiesByUser(request: Request): Response {
        logger.infoLogRequest(request)

        val userId = request.path("uid")

        val activities = activityServices.getActivitiesByUser(userId, request.pagination)
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .contentJson()
            .body(activitiesJson)
    }

    /**
     * Gets all existing activities.
     */
    private fun getAllActivities(request: Request): Response {
        logger.infoLogRequest(request)
        val activities = activityServices.getAllActivities(request.pagination)
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .contentJson()
            .body(activitiesJson)
    }

    /**
     * Handler for deleting an [ActivityDTO] using the information received in the path of the request.
     */
    private fun deleteActivity(request: Request): Response {
        logger.infoLogRequest(request)

        val activityId = request.path("aid")
        val sportID = request.path("sid")

        activityServices.deleteActivity(request.token, activityId, sportID)
        return Response(Status.NO_CONTENT)
    }

    /**
     * Handler for deleting [ActivityDTO]s using the information received in the path of the request.
     */
    private fun deleteActivities(request: Request): Response {
        logger.infoLogRequest(request)

        val activityIds = request.query("activityIDs")

        activityServices.deleteActivities(request.token, activityIds)
        return Response(Status.NO_CONTENT)
    }

    /**
     * Get the list of [User] that have an activity with the given sport and rid.
     */
    private fun getUsersByActivity(request: Request): Response {
        logger.infoLogRequest(request)
        val sportID = request.path("sid")
        val routeID = request.query("rid")

        val users = activityServices.getUsersByActivity(sportID, routeID, request.pagination)
        val bodyString = Json.encodeToString(UserListOutput(users))

        return Response(Status.OK)
            .contentJson()
            .body(bodyString)
    }

    /**
     * Updates an activity using the information received from the body of the request.
     */
    private fun updateActivity(request: Request): Response {
        logger.infoLogRequest(request)

        val activityBody = Json.decodeFromString<ActivityInput>(request.bodyString())
        val activityId = request.path("aid")
        val sportId = request.path("sid")

        activityServices.updateActivity(
            request.token,
            sportId,
            activityId,
            activityBody.duration,
            activityBody.date,
            activityBody.rid
        ) // TODO: Put parameters in objects

        return Response(Status.NO_CONTENT)
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
            request.pagination
        )
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .contentJson()
            .body(activitiesJson)
    }

    val handler = routes(
        "/sports/{sid}/activities" bind routes(
            "/" bind Method.POST to ::createActivity,
            "/{aid}" bind Method.DELETE to ::deleteActivity,
            "/{aid}" bind Method.GET to ::getActivity,
            "/" bind Method.GET to ::getActivitiesBySport,
            "/{aid}" bind Method.PUT to ::updateActivity
        ),
        "/users/{uid}/activities" bind Method.GET to ::getActivitiesByUser,
        "/sports/{sid}/users" bind Method.GET to ::getUsersByActivity,
        "/activities" bind routes(
            "/" bind Method.GET to ::getAllActivities,
            "/deletes" bind Method.POST to ::deleteActivities
        )

    )
}

/**
 * Gets the routes for the activities
 */
fun Activity(activityServices: ActivityServices): RoutingHttpHandler =
    ActivityRoutes(activityServices).handler
