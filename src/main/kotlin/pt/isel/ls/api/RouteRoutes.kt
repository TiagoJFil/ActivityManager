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
import pt.isel.ls.entities.Route
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID

class RouteRoutes(
    private val routeServices: RouteServices,
    private val userServices: UserServices
){
    @Serializable data class RouteList(val routes: List<Route>)
    @Serializable data class RouteCreationBody(
        val startLocation:String? = null,
        val endLocation: String? = null,
        val distance: Double?=null
    )
    @Serializable data class RouteIDResponse(val routeID: RouteID)
    /**
     * Gets all the routes.
     */
    private fun getRoutes(request: Request): Response{
        val routes = routeServices.getRoutes()
        val bodyString = Json.encodeToString(RouteList(routes))
        return Response(Status.OK).body(bodyString)
    }

    /**
     * Gets a route identified by the given [RouteID] from the query "id"
     */
    private fun getRoute(request: Request): Response{
        val routeID = request.path ("id")

        val route = routeServices.getRoute(routeID)

        val routeJson = Json.encodeToString(route)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(routeJson)
    }


    /**
     * Creates a route with the information that come in the body of the HTTP request.
     */
    private fun createRoute(request: Request): Response{
        val routeInfo = Json.decodeFromString<RouteCreationBody>(request.bodyString())
        val userId: UserID = userServices.getUserByToken(getToken(request))
        val routeId: RouteID =
            routeServices.createRoute(userId,routeInfo.startLocation, routeInfo.endLocation, routeInfo.distance)

        return Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(RouteIDResponse(routeId)))
    }


    val handler =
        "/routes" bind
                routes(
                    "/" bind Method.POST to ::createRoute,
                    "/" bind Method.GET to ::getRoutes,
                    "/{id}" bind Method.GET to ::getRoute,
                )

}

fun Route(routeServices: RouteServices, userServices: UserServices) =
    RouteRoutes(routeServices, userServices).handler