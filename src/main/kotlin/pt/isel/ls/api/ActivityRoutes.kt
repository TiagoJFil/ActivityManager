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
import pt.isel.ls.entities.Activity
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.UserID


class ActivityRoutes(
    private val activityServices: ActivityServices,
    private val sportsServices: SportsServices,
    private val userServices: UserServices
){
    @Serializable data class ActivityCreationBody(
        val duration: String? = null,
        val date: String? = null,
        val rid: String? = null,
    )
    @Serializable data class ActivityIdResponse(val activityID : String)

    /**
     * Creates an [Activity] using the information received in the path and body of the request.
     */
    private fun createActivity(request: Request): Response {
        val sportID = request.path("sid")

        val activityBody = Json.decodeFromString<ActivityCreationBody>(request.bodyString())
        val userId: UserID = userServices.getUserByToken(getToken(request))

        val activityId = activityServices.createActivity(userId, sportID, activityBody.duration, activityBody.date, activityBody.rid)
        return Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(ActivityIdResponse(activityId)))
    }

    /**
     * Gets the [Activity] with the given [ActivityID].
     */
    private fun getActivity(request: Request): Response {
        val activityId  = request.path("id")

        val activity = activityServices.getActivity(activityId)
        println(activity)
        val activityJson = Json.encodeToString<Activity>(activity)
        println(activityJson)
        return Response(Status.OK)
            .body(activityJson)
    }

    //TODO(/sports/{id}/activities)
    val handler =
        "/activity" bind
                routes(
                    "/{sid}" bind Method.POST to ::createActivity,
                    "/{id}" bind Method.GET to ::getActivity
                )

}

fun Activity(activityServices: ActivityServices, sportsServices: SportsServices, userServices: UserServices) =
    ActivityRoutes(activityServices,sportsServices,userServices).handler
