package pt.isel.ls.api

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.then
import org.http4k.routing.ResourceLoader
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.routing.static
import org.slf4j.LoggerFactory
import pt.isel.ls.config.Environment
import pt.isel.ls.service.AppError
import pt.isel.ls.service.AuthorizationError
import pt.isel.ls.service.InternalError
import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.service.MissingParameter
import pt.isel.ls.service.ResourceNotFound
import pt.isel.ls.service.UnauthenticatedError
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.utils.api.json
import pt.isel.ls.utils.logRequest
import pt.isel.ls.utils.warnResponse
import java.sql.SQLException
import kotlin.system.measureTimeMillis

private val eLogger = LoggerFactory.getLogger("ERRORS")
private val tLogger = LoggerFactory.getLogger("TIMER")
private val rLogger = LoggerFactory.getLogger("REQUESTS")

/**
 * Binds [routes] to "/api" and applies [onErrorFilter] in all of them
 *
 * @param routes routes to bind to /api
 */
fun getApiRoutes(routes: RoutingHttpHandler) = routes(

    "/api" bind routes.withFilter(loggingFilter.then(onErrorFilter).then(timeFilter)),
    static(Classpath("public")),
    singlePageApp(ResourceLoader.Directory("static-content")) // For SPA

)

/**
 * Serves swagger ui to a /docs route by redirecting to the swagger ui index.html public resource
 */
private fun swaggerUi() = routes(
    "/docs" bind Method.GET to {
        Response(Status.FOUND).header("Location", "/swagger-ui/index.html")
    }
)

/**
 * Gets all main app routes
 * @param env respective environment
 */
fun getAppRoutes(env: Environment) = routes(
    User(env.userServices),
    Route(env.routeServices),
    Sport(env.sportsServices),
    Activity(env.activityServices),
    swaggerUi()
)

private val statusMapping = mapOf(
    InternalError::class to INTERNAL_SERVER_ERROR,
    ResourceNotFound::class to NOT_FOUND,
    UnauthenticatedError::class to UNAUTHORIZED,
    AuthorizationError::class to FORBIDDEN,
    MissingParameter::class to BAD_REQUEST,
    InvalidParameter::class to BAD_REQUEST,
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
        } catch (appError: AppError) {
            val body = Json.encodeToString(HttpError(appError.code, appError.message))

            val status = statusMapping[appError::class] ?: error("Closed hierarchy. Null never expected")
            Response(status).json(body)
        } catch (serializerException: SerializationException) {

            val body = Json.encodeToString(HttpError(0, "Invalid body."))
            eLogger.warnResponse(BAD_REQUEST, "Invalid body.")
            Response(BAD_REQUEST).json(body)
        } catch (dbError: SQLException) {

            val body = Json.encodeToString(HttpError(2004, "Internal Error."))
            eLogger.warnResponse(INTERNAL_SERVER_ERROR, dbError.message ?: "Database Error")
            Response(INTERNAL_SERVER_ERROR).header("content-type", "application/json").body(body)
        } catch (e: Exception) {
            eLogger.error(e.stackTraceToString())
            val body = Json.encodeToString(HttpError(0, "Internal Error."))
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

private val loggingFilter = Filter { handler ->
    val handlerWrapper: HttpHandler = { request: Request ->
        rLogger.logRequest(request)
        handler(request)
    }
    handlerWrapper
}
