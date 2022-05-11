package pt.isel.ls.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.service.SportsServices
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.infoLogRequest

class SportRoutes(
    private val sportsServices: SportsServices
) {

    @Serializable data class SportCreationInput(val name: String? = null, val description: String? = null)
    @Serializable data class SportIDOutput(val sportID: SportID)
    @Serializable data class SportListOutput(val sports: List<SportDTO>)

    companion object {
        val logger = getLoggerFor<SportRoutes>()
    }

    /**
     * Create a new sport with the information from the body of the HTTP request.
     */
    private fun createSport(request: Request): Response {
        logger.infoLogRequest(request)

        val sportsBody = Json.decodeFromString<SportCreationInput>(request.bodyString())

        val token: UserToken? = getToken(request)
        val sportID = sportsServices.createSport(token, sportsBody.name, sportsBody.description)

        return Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(SportIDOutput(sportID)))
    }

    /**
     * Gets the sport that its given in the params of the path of the uri.
     */
    private fun getSport(request: Request): Response {
        logger.infoLogRequest(request)

        val sportID = request.path("sid")
        val sport = sportsServices.getSport(sportID)
        val sportJson = Json.encodeToString(sport)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(sportJson)
    }

    /**
     * Gets all the available sports.
     */
    private fun getSports(request: Request): Response {
        logger.infoLogRequest(request)
        val sports = sportsServices.getSports(PaginationInfo.fromRequest(request))
        val bodyString = Json.encodeToString(SportListOutput(sports))
        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(bodyString)
    }

    private fun updateSport(request: Request): Response {
        logger.infoLogRequest(request)
        TODO("Not yet implemented")
    }

    val handler = routes(
        "/sports" bind routes(
            "/" bind POST to ::createSport,
            "/{sid}" bind GET to ::getSport,
            "/" bind GET to ::getSports,
            "/" bind PUT to ::updateSport
        )
    )
}

fun Sport(sportsServices: SportsServices) =
    SportRoutes(sportsServices).handler
