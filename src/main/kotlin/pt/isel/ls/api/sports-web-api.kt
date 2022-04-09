package pt.isel.ls.api

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.slf4j.LoggerFactory
import pt.isel.ls.config.ServicesInfo
import pt.isel.ls.repository.database.utils.DataBaseAccessException
import pt.isel.ls.service.*
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.warnStatus
import kotlin.system.measureTimeMillis

/**
 * Binds [routes] to "/api" and applies [onErrorFilter] in all of them
 *
 * @param routes routes to bind to /api
 */
fun getApiRoutes(routes: RoutingHttpHandler) = routes(

    "/api" bind routes.withFilter(timeFilter).withFilter(onErrorFilter),
    static(Classpath("public"))
)

fun swaggerUi(htmlPath: String) = routes(
    "/docs" bind Method.GET to {
        Response(Status.FOUND).header("Location", htmlPath)
    }
)

/**
 * Gets all main app routes
 * @param env respective environment
 */
fun getAppRoutes(env: ServicesInfo) = routes(
    User(env.userServices),
    Route(env.routeServices),
    Sport(env.sportsServices),
    Activity(env.activityServices),
    swaggerUi("/swagger-ui/index.html")
)

private val eLogger = LoggerFactory.getLogger("pt.isel.ls.api.ERRORS")
private val tLogger = LoggerFactory.getLogger("pt.isel.ls.api.TIMER")

/**
 *
 * Catches app errors thrown on request handlers
 * and sends the respective status code
 * with an [HttpError] body.
 *
 */
private val onErrorFilter = Filter { handler ->
    val handlerWrapper: HttpHandler = { request ->
        try {
            handler(request)
        } catch (appError: AppError) {

            val body = Json.encodeToString(HttpError(appError.code, appError.message))
            val baseResponse = Response(BAD_REQUEST).header("content-type", "application/json").body(body)

            when (appError) {
                is ResourceNotFound -> {
                    eLogger.warnStatus(NOT_FOUND, appError.message ?: "Resource not found")
                    baseResponse.status(NOT_FOUND)
                }
                is UnauthenticatedError -> {
                    eLogger.warnStatus(UNAUTHORIZED, appError.message ?: "Unauthenticated")
                    baseResponse.status(UNAUTHORIZED)
                }
                is MissingParameter, is InvalidParameter -> {
                    eLogger.warnStatus(BAD_REQUEST, appError.message ?: "Invalid parameter")
                    baseResponse
                }
                is InternalError -> {
                    eLogger.warnStatus(INTERNAL_SERVER_ERROR, appError.message ?: "Internal error")
                    baseResponse.status(INTERNAL_SERVER_ERROR)
                }
            }
        } catch (serializerException: SerializationException) {

            val body = Json.encodeToString(HttpError(0, "Invalid body."))
            eLogger.warnStatus(BAD_REQUEST, "Invalid body.")
            Response(BAD_REQUEST).header("content-type", "application/json").body(body)
        } catch (dbError: DataBaseAccessException) {
            val body = Json.encodeToString(HttpError(0, "Internal Error."))
            eLogger.error(dbError.message)
            Response(INTERNAL_SERVER_ERROR).header("content-type", "application/json").body(body)
        }
    }
    handlerWrapper
}

private val timeFilter = Filter { handler ->
    val handlerWrapper: HttpHandler = { request: Request ->
        val returnedValue: Response
        val time = measureTimeMillis {
            returnedValue = handler(request)
        }
        tLogger.info("Request from ${request.source?.address}:${request.source?.port} to ${request.uri} took $time ms")
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
