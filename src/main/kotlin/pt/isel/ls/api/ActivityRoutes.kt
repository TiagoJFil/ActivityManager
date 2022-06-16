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
import pt.isel.ls.service.ActivityServices
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.inputs.ActivityInputs.ActivityCreateInput
import pt.isel.ls.service.inputs.ActivityInputs.ActivitySearchInput
import pt.isel.ls.service.inputs.ActivityInputs.ActivityUpdateInput
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.api.bearerToken
import pt.isel.ls.utils.api.json
import pt.isel.ls.utils.api.pagination

class ActivityRoutes(
    private val activityServices: ActivityServices
) {
    @Serializable
    data class ActivityInput(
        val duration: Param = null,
        val date: Param = null,
        val rid: Param = null,
    )

    @Serializable
    data class ActivityIDOutput(val activityID: ActivityID)

    @Serializable
    data class ActivityListOutput(val activities: List<ActivityDTO>)

    /**
     * Creates an [ActivityDTO] using the information received in the path and body of the request.
     */
    private fun createActivity(request: Request): Response {

        val sportID = request.path("sid")

        val activityBody = Json.decodeFromString<ActivityInput>(request.bodyString())

        val activityId = activityServices.createActivity(
            request.bearerToken,
            ActivityCreateInput(sportID, activityBody.duration, activityBody.date, activityBody.rid)
        )

        val output = Json.encodeToString(ActivityIDOutput(activityId))

        return Response(Status.CREATED)
            .json(output)
    }

    /**
     * Gets the [ActivityDTO] with the given [ActivityID].
     */
    private fun getActivity(request: Request): Response {

        val activityId = request.path("aid")
        val sportId = request.path("sid")

        val activity = activityServices.getActivity(activityId, sportId)

        val activityJson = Json.encodeToString<ActivityDTO>(activity)

        return Response(Status.OK)
            .json(activityJson)
    }

    /**
     * Gets all the activities created by the user that matches the given id.
     */
    private fun getActivitiesByUser(request: Request): Response {

        val userId = request.path("uid")

        val activities = activityServices.getActivitiesByUser(userId, request.pagination)
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .json(activitiesJson)
    }

    /**
     * Gets all existing activities.
     */
    private fun getAllActivities(request: Request): Response {
        val activities = activityServices.getAllActivities(request.pagination)
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .json(activitiesJson)
    }

    /**
     * Handler for deleting an [ActivityDTO] using the information received in the path of the request.
     */
    private fun deleteActivity(request: Request): Response {

        val activityId = request.path("aid")
        val sportID = request.path("sid")

        activityServices.deleteActivity(request.bearerToken, activityId, sportID)
        return Response(Status.NO_CONTENT)
    }

    /**
     * Handler for deleting [ActivityDTO]s using the information received in the path of the request.
     */
    private fun deleteActivities(request: Request): Response {

        val activityIds = request.query("activityIDs")

        activityServices.deleteActivities(request.bearerToken, activityIds)
        return Response(Status.NO_CONTENT)
    }

    /**
     * Updates an activity using the information received from the body of the request.
     */
    private fun updateActivity(request: Request): Response {

        val activityBody = Json.decodeFromString<ActivityInput>(request.bodyString())
        val activityId = request.path("aid")
        val sportId = request.path("sid")

        activityServices.updateActivity(
            request.bearerToken,
            ActivityUpdateInput(
                sportId,
                activityId,
                activityBody.duration,
                activityBody.date,
                activityBody.rid
            )
        )
        return Response(Status.NO_CONTENT)
    }

    /**
     * Gets all the activities of the sport identified by the id given in the params of the uri's path.
     */
    private fun getActivitiesBySport(request: Request): Response {

        val order = request.query("orderBy")
        val date = request.query("date")
        val routeID = request.query("rid")
        val sportID = request.path("sid")

        val activities = activityServices.getActivities(
            ActivitySearchInput(
                sportID,
                order,
                date,
                routeID,
            ),
            request.pagination
        )
        val activitiesJson = Json.encodeToString(ActivityListOutput(activities))

        return Response(Status.OK)
            .json(activitiesJson)
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
