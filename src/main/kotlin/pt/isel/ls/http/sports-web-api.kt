package pt.isel.ls.http

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.lens.Missing
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.services.*

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
        }catch(appError: AppError){

            val body = Json.encodeToString(HttpError(appError.code, appError.message))
            val baseResponse = Response(INTERNAL_SERVER_ERROR).body(body)

            when (appError) {
                is MissingParameter, is InvalidParameter -> baseResponse.status(BAD_REQUEST)
                is ResourceNotFound -> baseResponse.status(NOT_FOUND)
            }

        }
    }
    handlerWrapper
}
