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
import org.slf4j.LoggerFactory
import pt.isel.ls.services.dto.HttpError
import pt.isel.ls.services.*
import pt.isel.ls.utils.Environment
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.warnStatus
import kotlin.system.measureTimeMillis

/**
 * Binds [routes] to "/api" and applies [onErrorFilter] in all of them
 *
 * @param routes routes to bind to /api
 */
fun getApiRoutes(routes: RoutingHttpHandler) = routes(

    "/api" bind routes.withFilter(timeFilter).withFilter(onErrorFilter)

)

/**
 * Gets all main app routes
 * @param env respective environment
 */
fun getAppRoutes(env: Environment) = routes(
    User(env.userServices),
    Route(env.routeServices),
    Sport(env.sportsServices),
    Activity(env.activityServices)
)
private val logger =  LoggerFactory.getLogger("pt.isel.ls.api.sports-web-api")

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
                is ResourceNotFound ->{
                    logger.warnStatus(NOT_FOUND, appError.message ?: "Resource not found")
                    baseResponse.status(NOT_FOUND)
                }
                is UnauthenticatedError -> {
                    logger.warnStatus(UNAUTHORIZED, appError.message ?: "Unauthenticated")
                    baseResponse.status(UNAUTHORIZED)
                }
                is MissingParameter, is InvalidParameter -> {
                    logger.warnStatus(BAD_REQUEST, appError.message ?: "Invalid parameter")
                    baseResponse
                }
            }

        }catch (serializerException: SerializationException){
            val body = Json.encodeToString(HttpError(0, "Invalid body."))
            logger.warnStatus(BAD_REQUEST,"Invalid body.")
            Response(BAD_REQUEST).header("content-type", "application/json").body(body)
        }
    }
    handlerWrapper
}

private val timeFilter = Filter { handler ->
    val handlerWrapper : HttpHandler = { request: Request ->
        val returnedValue : Response;
        val time = measureTimeMillis {
            returnedValue =handler(request)
        }
        logger.info("Request from [${request.source}] to uri: [${request.uri}] took $time ms")
        returnedValue
    }
    handlerWrapper
}

/**
 * Gets the user token from the request
 *
 * @param request request to get the token from
 * @return the user token or null if not found or in invalid format
 */
fun getToken(request: Request): UserToken? =
    request
        .header("Authorization")
        ?.substringAfter("Bearer ", missingDelimiterValue = "")
        ?.ifBlank { null }
