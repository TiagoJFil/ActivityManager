package pt.isel.ls.http

import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.UserServices

/**
 *
 * Binds [routes] to "/api"
 *
 * @param routes API main routes as default, parameter used to mock routes in testing
 */
fun getApiRoutes(routes: RoutingHttpHandler) = routes(

    "/api" bind routes.withFilter(onErrorFilter)

)

fun getRoutes(userServices: UserServices, routeServices: RouteServices) = routes(
    userRoutes(userServices),
    routeRoutes(routeServices, userServices)
)

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




