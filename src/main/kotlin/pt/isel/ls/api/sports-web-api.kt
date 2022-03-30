package pt.isel.ls.api

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.services.dto.HttpError
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.services.*
import pt.isel.ls.utils.Environment
import pt.isel.ls.utils.EnvironmentType
import pt.isel.ls.utils.UserToken

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
 * Gets all main app routes
 * @param userServices   user services
 * @param routeServices  route services
 * @param sportsServices sports services
 */
fun getAppRoutes(userServices: UserServices, routeServices: RouteServices, sportsServices: SportsServices, activityServices : ActivityServices) = routes(
    User(userServices, activityServices),
    Route(routeServices, userServices),
    Sport(sportsServices, userServices, activityServices),
    Activity(activityServices, userServices)
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
            val baseResponse = Response(BAD_REQUEST).header("content-type", "application/json").body(body)

            when (appError) {
                is ResourceNotFound -> baseResponse.status(NOT_FOUND)
                is UnauthenticatedError -> baseResponse.status(UNAUTHORIZED)
                is MissingParameter, is InvalidParameter -> baseResponse
            }

        }catch (serializerException: SerializationException){
            val body = Json.encodeToString(HttpError(0, "Invalid body."))
            Response(BAD_REQUEST).header("content-type", "application/json").body(body)
        }
    }
    handlerWrapper
}

/**
 * Gets the user token from the request
 *
 * TODO: PASS TOKEN TO SERVICES AND CONVERT TO USERID
 *
 * @param request request to get the token from
 * @return the user token or null if not found or in invalid format
 */
fun getToken(request: Request): UserToken? =
    request
        .header("Authorization")
        ?.substringAfter("Bearer ", missingDelimiterValue = "")
        ?.ifBlank { null }



