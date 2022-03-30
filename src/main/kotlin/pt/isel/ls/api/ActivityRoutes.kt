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
import pt.isel.ls.services.dto.ActivityDTO
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID


class ActivityRoutes(
    val activityServices: ActivityServices,
    val userServices: UserServices
){
    @Serializable data class ActivityCreationBody(
        val duration: String? = null,
        val date: String? = null,
        val rid: RouteID? = null,
    )
    @Serializable data class ActivityIDResponse(val activityID : ActivityID)

    /**
     * Creates an [ActivityDTO] using the information received in the path and body of the request.
     */
    private fun createActivity(request: Request): Response {
        val sportID = request.path("sid")

        val activityBody = Json.decodeFromString<ActivityCreationBody>(request.bodyString())
        val token: UserToken? = getToken(request)

        val activityId = activityServices.createActivity(userId, sportID, activityBody.duration, activityBody.date, activityBody.rid)
        return Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(ActivityIDResponse(activityId)))
    }

    /**
     * Gets the [ActivityDTO] with the given [ActivityID].
     */
    private fun getActivity(request: Request): Response {
        val activityId  = request.path("id")

        val activity = activityServices.getActivity(activityId)

        val activityJson = Json.encodeToString<Activity>(activity)
        println(activityJson)
        return Response(Status.OK)
                .header("content-type", "application/json")
                .body(activityJson)
    }

    /**
     * Gets all the activities created by the user that matches the given id.
     */
    private fun getActivitiesByUser(request: Request): Response {
        val userId = request.path("uid")

        val activities = activityServices.getActivitiesByUser(userId)
        val activitiesJson = Json.encodeToString(ActivityList(activities))

        return Response(Status.OK)
                .header("content-type", "application/json")
                .body(activitiesJson)
    }

    /**
     * Handler for deleting an [ActivityDTO] using the information received in the path of the request.
     */
    private fun deleteActivity(request: Request): Response {
        val activityId = request.path("aid")
        val sportID = request.path("sid")

        val token: UserToken? = getToken(request)

        activityServices.deleteActivity(token, activityId, sportID)
        return Response(Status.NO_CONTENT)
    }

    val handler = routes(
            "/sports/{sid}/activities" bind routes(
                        "/" bind Method.POST to ::createActivity,
                        "/{aid}" bind Method.DELETE to ::deleteActivity,
                        "/{aid}" bind Method.GET to ::getActivity,
                        "/" bind Method.GET to ::getActivitiesBySport
                    ),
            "/users/{uid}/activities" bind Method.GET to ::getActivitiesByUser
        )

}

fun Activity(activityServices: ActivityServices, userServices: UserServices) =
    ActivityRoutes(activityServices, userServices).handler
