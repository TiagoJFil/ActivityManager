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
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.UserServices
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
    @Serializable
    data class ActivityIDResponse(val activityID : ActivityID)

    /**
     * Creates an [Activity] using the information received in the path and body of the request.
     */
    private fun createActivity(request: Request): Response {
        val sportID = request.path("sid")

        val activityBody = Json.decodeFromString<ActivityCreationBody>(request.bodyString())
        val userId: UserID = userServices.getUserByToken(getToken(request))

        val activityId = activityServices.createActivity(userId, sportID, activityBody.duration, activityBody.date, activityBody.rid)
        return Response(Status.CREATED)
            .body(Json.encodeToString(ActivityIDResponse(activityId)))
    }

    /**
     * Handler for deleting an [Activity] using the information received in the path of the request.
     */
    private fun deleteActivity(request: Request): Response {
        val activityId = request.path("activityID")
        val sportID = request.path("sid")
        val userId: UserID = userServices.getUserByToken(getToken(request))
        activityServices.deleteActivity(userId, activityId, sportID)
        return Response(Status.OK)
    }

    //TODO(/sports/{id}/activities)
    val handler =
        "/activity" bind
                routes(
                    "/{sid}" bind Method.POST to ::createActivity,
                    "/{sid}/{activityID}" bind Method.DELETE to ::deleteActivity
                )

}

fun Activity(activityServices: ActivityServices, userServices: UserServices) =
    ActivityRoutes(activityServices, userServices).handler
