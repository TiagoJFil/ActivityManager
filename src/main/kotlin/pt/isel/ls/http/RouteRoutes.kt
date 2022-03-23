package pt.isel.ls.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.entities.Route
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.services.RouteServices

class RouteRoutes(
    val routeServices: RouteServices
){

    @Serializable data class RouteList(val routes: List<Route>)
    private fun getRoutes(request: Request): Response{
        val routes = routeServices.getRoutes()
        val bodyString = Json.encodeToString(RouteList(routes))
        return Response(Status.OK).body(bodyString)
    }

    private fun getRoute(request: Request): Response{
        TODO()
    }

    private fun createRoute(request: Request): Response{
        TODO()
    }


    val handler =
        "/routes" bind
                routes(
                    "/" bind Method.POST to ::createRoute,
                    "/" bind Method.GET to ::getRoutes,
                    "/{id}" bind Method.GET to ::getRoute,
                )

}

fun routeRoutes(routeServices: RouteServices) = RouteRoutes(routeServices).handler