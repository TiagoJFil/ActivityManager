package pt.isel.ls.http

import kotlinx.serialization.Serializable
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
import pt.isel.ls.entities.Route
import pt.isel.ls.entities.Sport
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices

class SportRoutes(
    val sportsServices: SportsServices,
    val userServices: UserServices
) {
    @Serializable
    data class SportList(val routes: List<Sport>)

    private fun createSport(request: Request): Response{
        TODO()
    }

    /**
     * Gets the sport that its given in the params of the path of the uri
     */
    private fun getSport(request: Request): Response {
        val sportID = request.path ("id")
        val sport = sportsServices.getSport(sportID)
        val sportJson = Json.encodeToString(sport)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(sportJson)
    }

    private fun getSports(request: Request): Response {
        val sports = sportsServices.getSports()
        val bodyString = Json.encodeToString<SportList>(SportList(sports))
        return Response(Status.OK).body(bodyString)
    }


    val handler = routes(
        "/sports" bind routes(
            "/" bind POST to ::createSport,
            "/{id}" bind GET to ::getSport,
            "/" bind GET to ::getSports,
        )
    )

}

fun sportsRoutes(sportsServices: SportsServices, userServices: UserServices) =
    SportRoutes(sportsServices, userServices).handler