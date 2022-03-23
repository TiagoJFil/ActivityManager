package pt.isel.ls.http

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.services.RouteServices


class RouteRoutes(
    val routeServices: RouteServices = RouteServices(RouteDataMemRepository())
){
    private fun getRoute(request: Request): Response {
        val routeID = request.path ("id")

        val route = routeServices.getRoute(routeID)

        val routeJson = Json.encodeToString(route)

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(routeJson)
    }
}
