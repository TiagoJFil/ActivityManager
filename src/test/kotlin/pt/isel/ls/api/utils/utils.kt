package pt.isel.ls.api.utils

import org.http4k.core.Headers
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import pt.isel.ls.api.ActivityRoutes.ActivityCreationBody
import pt.isel.ls.api.ActivityRoutes.ActivityIDResponse
import pt.isel.ls.api.RouteRoutes.RouteCreationBody
import pt.isel.ls.api.RouteRoutes.RouteIDResponse
import pt.isel.ls.api.SportRoutes.SportCreationBody
import pt.isel.ls.api.SportRoutes.SportIDResponse
import pt.isel.ls.api.UserRoutes.*
import pt.isel.ls.config.ServicesInfo
import pt.isel.ls.config.EnvironmentType
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.getEnv
import pt.isel.ls.utils.*

const val ROUTE_PATH = "/api/routes/"
const val USER_PATH = "/api/users/"
const val ACTIVITY_PATH = "/api/sports/"
const val SPORT_PATH = "/api/sports/"

val TEST_ENV: ServicesInfo
                get()= EnvironmentType.TEST.getEnv()

val INTEGRATION_TEST_ENV : ServicesInfo
        get() = EnvironmentType.INTEGRATION_TEST.getEnv()


/**
 * Returns a list of headers with the authorization header injected with the given token.
 * @param token the token to be injected.
 */
fun authHeader(token: String): Headers = listOf("Authorization" to  "Bearer $token")


/**
 * Helper function to create a user, ensures it is created and returns the respective [UserIDResponse]
 * @param userCreationBody the body of the user to be created. Must be valid.
 */
fun RoutingHttpHandler.createUser(userCreationBody: UserCreationBody): UserIDResponse =
        postRequest<UserCreationBody, UserIDResponse>(
                this,
                USER_PATH,
                userCreationBody,
                authHeader(GUEST_TOKEN),
                Response::expectCreated
        )

/**
 * Helper function to create an activity, ensures it is created and returns the respective [ActivityIDResponse]
 * @param activityCreationBody the body of the activity to be created. Must be valid.
 */
fun RoutingHttpHandler.createActivity(
        activityCreationBody: ActivityCreationBody,
        sportID: SportID,
        token: String = GUEST_TOKEN
): ActivityIDResponse
        = postRequest<ActivityCreationBody, ActivityIDResponse>(
        this,
        "$ACTIVITY_PATH${sportID}/activities",
        activityCreationBody,
        headers = authHeader(token),
        Response::expectCreated
)

/**
 * Helper function to create a sport, ensures it is created and returns the respective [SportIDResponse]
 * @param sportCreationBody the body of the sport to be created. Must be valid.
 */
fun RoutingHttpHandler.createSport(sportCreationBody: SportCreationBody): SportIDResponse
        = postRequest<SportCreationBody, SportIDResponse>(
        this,
        SPORT_PATH,
        sportCreationBody,
        headers = authHeader(GUEST_TOKEN),
        Response::expectCreated
)

/**
 * Helper function to create a route, ensures it is created and returns the respective [RouteIDResponse]
 * @param routeCreation the route creation body. Must be valid.
 */
fun RoutingHttpHandler.createRoute(routeCreation: RouteCreationBody): RouteIDResponse
        = postRequest<RouteCreationBody, RouteIDResponse>(
        this,
        ROUTE_PATH,
        routeCreation,
        authHeader(GUEST_TOKEN),
        Response::expectCreated
)