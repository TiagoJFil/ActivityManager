package pt.isel.ls.http

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices

/**
 *
 * Binds [routes] to "/api"
 *
 * @param routes routes to bind to /api
 */
fun getApiRoutes(routes: RoutingHttpHandler) = routes(

    "/api" bind routes.withFilter(onErrorFilter)

)

/**
 * Gets all main routes from the api Services
 * @param userServices   user services
 * @param routeServices  route services
 * @param sportsServices sports services
 */
fun getAppRoutes(userServices: UserServices, routeServices: RouteServices, sportsServices: SportsServices) = routes(
    userRoutes(userServices),
    routeRoutes(routeServices, userServices),
    sportsRoutes(sportsServices, userServices)
)

private val onErrorFilter = Filter { handler ->
    val handlerWrapper: HttpHandler = { request ->
        try {
            handler(request)
        } catch (e: IllegalArgumentException) {
            Response(Status.BAD_REQUEST).body(e.message.toString())
        } catch (e: IllegalStateException) {
            Response(Status.NOT_FOUND).body(e.message.toString())
        }
    }
    handlerWrapper
}
