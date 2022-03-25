package pt.isel.ls.http

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.lens.Missing
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.services.*
import java.net.ResponseCache

/**
 *
 * Binds [routes] to "/api" and applies [onErrorFilter] in all of them
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
fun getAppRoutes(userServices: UserServices, routeServices: RouteServices, sportsServices: SportsServices, activityServices : ActivityServices) = routes(
    User(userServices),
    Route(routeServices, userServices),
    Sport(sportsServices, userServices),
    activityRoutes(activityServices,sportsServices,userServices)
)


/**
 * Catches app errors thrown on request handlers
 * and sends the respective status code
 * with an [HttpError] body.
 */
private val onErrorFilter = Filter { handler ->
    val handlerWrapper: HttpHandler = { request ->
        try {
            handler(request)
        }catch(appError: AppError){

            val body = Json.encodeToString(HttpError(appError.code, appError.message))
            val baseResponse = Response(BAD_REQUEST).body(body)

            when (appError) {
                is ResourceNotFound -> baseResponse.status(NOT_FOUND)
                is MissingParameter, is InvalidParameter -> baseResponse
            }

        }catch (serializerException: SerializationException){
            val body = Json.encodeToString(HttpError(0, "Invalid body."))
            Response(BAD_REQUEST).body(body)
        }
    }
    handlerWrapper
}
