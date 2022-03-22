package pt.isel.ls.http



import org.http4k.core.*
import org.http4k.core.Status.Companion.MOVED_PERMANENTLY
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

private val routeRoutes = routes(
    "/" bind Method.POST to {Response(MOVED_PERMANENTLY)},
    "/" bind Method.GET to {Response(MOVED_PERMANENTLY)},
    "/{id}" bind Method.GET to {Response(MOVED_PERMANENTLY)},
)

private fun getRoutes(userRoutes: RoutingHttpHandler) = routes(
    "/users" bind userRoutes,
    "/routes" bind routeRoutes,
)

fun getApiRoutes(
    userRoutes: RoutingHttpHandler=userRoutes()
) = routes("/api" bind getRoutes(userRoutes)).withFilter(onErrorFilter)

private val onErrorFilter = Filter { handler ->
    val handlerWrapper: HttpHandler = { request ->
        try{
            handler(request)
        }catch(e: IllegalArgumentException){
            Response(Status.BAD_REQUEST).body(e.message.toString())
        }catch(e: IllegalStateException){
            Response(Status.NOT_FOUND).body(e.message.toString())
        }
    }
    handlerWrapper
}




