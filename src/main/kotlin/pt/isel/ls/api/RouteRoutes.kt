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
import pt.isel.ls.service.RouteServices
import pt.isel.ls.service.dto.RouteDTO
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.api.contentJson
import pt.isel.ls.utils.api.pagination
import pt.isel.ls.utils.api.token
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.infoLogRequest

class RouteRoutes(
    private val routeServices: RouteServices,

) {
    @Serializable data class RouteListOutput(val routes: List<RouteDTO>)
    @Serializable data class RouteInput(
        val startLocation: Param = null,
        val endLocation: Param = null,
        val distance: Float? = null
    )
    @Serializable data class RouteIDOutput(val routeID: RouteID)
    companion object {
        private val logger = getLoggerFor<RouteRoutes>()
    }

    /**
     * Gets all the routes.
     */
    private fun getRoutes(request: Request): Response {
        logger.infoLogRequest(request)

        val endLocationQuery = request.query("endLocation")
        val startLocationQuery = request.query("startLocation")

        val routes = routeServices.getRoutes(request.pagination, startLocationQuery, endLocationQuery)

        val bodyString = Json.encodeToString(RouteListOutput(routes))
        return Response(Status.OK).contentJson().body(bodyString)
    }

    /**
     * Gets a route identified by the given [RouteID] from the query "id"
     */
    private fun getRoute(request: Request): Response {
        logger.infoLogRequest(request)

        val routeID = request.path("rid")

        val route = routeServices.getRoute(routeID)

        val routeJson = Json.encodeToString(route)

        return Response(Status.OK)
            .contentJson()
            .body(routeJson)
    }

    /**
     * Creates a route with the information that come in the body of the HTTP request.
     */
    private fun createRoute(request: Request): Response {
        logger.infoLogRequest(request)

        val routeInfo = Json.decodeFromString<RouteInput>(request.bodyString())
        val routeId: RouteID = routeServices.createRoute(
            request.token,
            routeInfo.startLocation,
            routeInfo.endLocation,
            routeInfo.distance
        )

        return Response(Status.CREATED)
            .contentJson()
            .body(Json.encodeToString(RouteIDOutput(routeId)))
    }

    /**
     * Updates a route identified by the given [RouteID] from the query "rid"
     */
    private fun updateRoute(request: Request): Response {
        logger.infoLogRequest(request)

        val routeID = request.path("rid")
        val routeInfo = Json.decodeFromString<RouteInput>(request.bodyString())

        routeServices.updateRoute(
            request.token,
            routeID,
            routeInfo.startLocation,
            routeInfo.endLocation,
            routeInfo.distance
        )

        return Response(Status.NO_CONTENT)
    }

    val handler =
        "/routes" bind
            routes(
                "/" bind Method.POST to ::createRoute,
                "/" bind Method.GET to ::getRoutes,
                "/{rid}" bind Method.GET to ::getRoute,
                "/{rid}" bind Method.PUT to ::updateRoute,
            )
}

fun Route(routeServices: RouteServices) =
    RouteRoutes(routeServices).handler
