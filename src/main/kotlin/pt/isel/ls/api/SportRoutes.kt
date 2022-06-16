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
import pt.isel.ls.utils.api.bearerToken
import pt.isel.ls.utils.api.json
import pt.isel.ls.utils.api.pagination
import pt.isel.ls.utils.logRequest
import pt.isel.ls.utils.loggerFor

class SportRoutes(
    private val sportsServices: SportsServices
) {

    @Serializable data class SportInput(val name: String? = null, val description: String? = null)
    @Serializable data class SportIDOutput(val sportID: SportID)
    @Serializable data class SportListOutput(val sports: List<SportDTO>)

    companion object {
        private val logger = loggerFor<SportRoutes>()
    }

    /**
     * Create a new sport with the information from the body of the HTTP request.
     */
    private fun createSport(request: Request): Response {
        logger.logRequest(request)

        val sportsBody = Json.decodeFromString<SportInput>(request.bodyString())
        val sportID = sportsServices.createSport(request.bearerToken, sportsBody.name, sportsBody.description)

        val sportIDJson = Json.encodeToString(SportIDOutput(sportID))

        return Response(Status.CREATED)
            .json(sportIDJson)
    }

    /**
     * Gets the sport that its given in the params of the path of the uri.
     */
    private fun getSport(request: Request): Response {
        logger.logRequest(request)

        val sportID = request.path("sid")
        val sport = sportsServices.getSport(sportID)

        val sportJson = Json.encodeToString(sport)

        return Response(Status.OK)
            .json(sportJson)
    }

    /**
     * Gets all the available sports.
     */
    private fun getSports(request: Request): Response {
        logger.logRequest(request)

        val search = request.query("search")

        val sports = sportsServices.getSports(search, request.pagination)
        val listJson = Json.encodeToString(SportListOutput(sports))
        return Response(Status.OK)
            .json(listJson)
    }

    /**
     * Updates a sport with the information from the body of the HTTP request.
     */
    private fun updateSport(request: Request): Response {
        logger.logRequest(request)

        val sportBody = Json.decodeFromString<SportInput>(request.bodyString())

        val sportID = request.path("sid")

        sportsServices.updateSport(request.bearerToken, sportID, sportBody.name, sportBody.description)

        return Response(Status.NO_CONTENT)
    }

    val handler = routes(
        "/sports" bind routes(
            "/" bind POST to ::createSport,
            "/{sid}" bind GET to ::getSport,
            "/" bind GET to ::getSports,
            "/{sid}" bind PUT to ::updateSport
        )
    )
}

fun Sport(sportsServices: SportsServices) =
    SportRoutes(sportsServices).handler
