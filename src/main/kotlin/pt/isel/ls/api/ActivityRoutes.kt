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
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.UserID


class ActivityRoutes(
    val activityServices: ActivityServices,
    val sportsServices: SportsServices,
    val userServices: UserServices
){
    @Serializable
    data class ActivityCreation(
        val duration: String? = null,
        val date: String? = null,
        val rid: String? = null,
    )
    @Serializable
    data class ActivityIdResponse(val activityID : String)

    private fun createActivity(request: Request): Response {
        val sportID = request.path("sid")
        if(sportID == null) throw  IllegalArgumentException() //TODO: Throw InvalidParameter error in Activity services


        val activityBody = Json.decodeFromString<ActivityCreation>(request.bodyString())
        val userId: UserID = userServices.getUserByToken(getToken(request))

        val activityId = activityServices.createActivity(userId, sportID, activityBody.duration, activityBody.date, activityBody.rid)
            return Response(Status.CREATED)
                .body(Json.encodeToString(ActivityIdResponse(activityId)))

    }



    //TODO(/sports/{id}/activities)
    val handler =
        "/activity" bind
                routes(
                    "/{sid}" bind Method.POST to ::createActivity,
                )

}

fun Activity(activityServices: ActivityServices, sportsServices: SportsServices, userServices: UserServices) =
    ActivityRoutes(activityServices,sportsServices,userServices).handler
