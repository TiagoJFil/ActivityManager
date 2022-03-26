package pt.isel.ls.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.entities.Sport
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.SportID

class SportRoutes(
    val sportsServices: SportsServices,
    val userServices: UserServices,
    val activityServices: ActivityServices
) {

    @Serializable
    data class SportCreationBody(val name: String? = null, val description: String? = null)
    @Serializable
    data class SportIDResponse(val sportID: SportID)

    /**
     * Create a new sport with the information from the body of the HTTP request.
     */
    private fun createSport(request: Request): Response {
        val sportsBody = Json.decodeFromString<SportCreationBody>(request.bodyString())

        val userID = userServices.getUserByToken(getToken(request))
        val sportID = sportsServices.createSport(userID, sportsBody.name, sportsBody.description)

        return Response(Status.CREATED)
            .body(Json.encodeToString(SportIDResponse(sportID)))
    }

    /**
     * Gets the sport that its given in the params of the path of the uri.
     */
    private fun getSport(request: Request): Response {
        val sportID = request.path ("id")
        val sport = sportsServices.getSport(sportID)
        val sportJson = Json.encodeToString(sport)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(sportJson)
    }

    @Serializable
    data class SportList(val sports: List<Sport>)

    /**
     * Gets all the available sports.
     */
    private fun getSports(request: Request): Response {
        val sports = sportsServices.getSports()
        val bodyString = Json.encodeToString(SportList(sports))
        return Response(Status.OK)
            .body(bodyString)
    }

    private fun getSportActivities(request: Request): Response {
        val sportID = request.path("id")
        val activities = activityServices.getSportActivities(sportID)
        val activitiesJson = Json.encodeToString(activities)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(activitiesJson)
    }


    val handler = routes(
        "/sports" bind routes(
            "/" bind POST to ::createSport,
            "/{id}" bind GET to ::getSport,
            "/" bind GET to ::getSports,
            "/{id]/activities"  bind GET to ::getSportActivities
        )
    )


}

fun Sport(sportsServices: SportsServices, userServices: UserServices, activityServices: ActivityServices) =
    SportRoutes(sportsServices, userServices, activityServices).handler