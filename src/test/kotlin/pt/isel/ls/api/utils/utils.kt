package pt.isel.ls.api.utils

import org.http4k.core.Headers
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import pt.isel.ls.api.ActivityRoutes.ActivityIDOutput
import pt.isel.ls.api.ActivityRoutes.ActivityInput
import pt.isel.ls.api.RouteRoutes.RouteIDOutput
import pt.isel.ls.api.RouteRoutes.RouteInput
import pt.isel.ls.api.SportRoutes.SportIDOutput
import pt.isel.ls.api.SportRoutes.SportInput
import pt.isel.ls.api.UserRoutes.AuthOutput
import pt.isel.ls.api.UserRoutes.UserInput
import pt.isel.ls.config.Environment
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.getEnv
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserToken

val ROUTE_PATH = EndPoints.ROUTE.path
val USER_PATH = EndPoints.USER.path
val ACTIVITY_PATH = EndPoints.ACTIVITY.path
val SPORT_PATH = EndPoints.SPORT.path

enum class EndPoints(val path: String) {
    ROUTE("/api/routes/"),
    USER("/api/users/"),
    ACTIVITY("/api/sports/"),
    SPORT("/api/sports/")
}

val TEST_ENV: Environment
    get() = getEnv()

/**
 * Returns a list of headers with the authorization header injected with the given token.
 * @param token the token to be injected.
 */
fun authHeader(token: String): Headers = listOf("Authorization" to "Bearer $token")

/**
 * Helper function to create a user, ensures it is created and returns the respective [AuthOutput]
 * @param userCreationBody the body of the user to be created. Must be valid.
 */
fun HttpHandler.createUser(userCreationBody: UserInput): AuthOutput =
    postRequest<UserInput, AuthOutput>(
        this,
        USER_PATH,
        userCreationBody,
        expectedStatus = Response::expectCreated
    )

/**
 * Helper function to create an activity, ensures it is created and returns the respective [ActivityIDOutput]
 * @param activityCreationBody the body of the activity to be created. Must be valid.
 */
fun HttpHandler.createActivity(
    activityCreationBody: ActivityInput,
    sportID: SportID,
    token: String = GUEST_TOKEN
): ActivityIDOutput =
    postRequest<ActivityInput, ActivityIDOutput>(
        this,
        "$ACTIVITY_PATH$sportID/activities",
        activityCreationBody,
        headers = authHeader(token),
        Response::expectCreated
    )

/**
 * Helper function to create a sport, ensures it is created and returns the respective [SportIDOutput]
 * @param sportCreationBody the body of the sport to be created. Must be valid.
 */
fun HttpHandler.createSport(sportCreationBody: SportInput): SportIDOutput =
    postRequest<SportInput, SportIDOutput>(
        this,
        SPORT_PATH,
        sportCreationBody,
        headers = authHeader(GUEST_TOKEN),
        Response::expectCreated
    )

/**
 * Helper function to create a route, ensures it is created and returns the respective [RouteIDOutput]
 * @param routeCreation the route creation body. Must be valid.
 */
fun HttpHandler.createRoute(routeCreation: RouteInput): RouteIDOutput =
    postRequest<RouteInput, RouteIDOutput>(
        this,
        ROUTE_PATH,
        routeCreation,
        authHeader(GUEST_TOKEN),
        Response::expectCreated
    )

/**
 * Helper function to update an activity
 * @param input the update body. Must be valid type.
 * @param resourceID the resource id to updated.
 */
inline fun <reified T, Tid> HttpHandler.updateResource(input: T, resourceID: Tid, token: UserToken = GUEST_TOKEN) {
    val path = when (input) {
        is ActivityInput -> "$ACTIVITY_PATH$resourceID"
        is RouteInput -> "$ROUTE_PATH$resourceID"
        is SportInput -> "$SPORT_PATH$resourceID"
        else -> throw IllegalArgumentException("Invalid input type")
    }
    putRequest<T>(
        this,
        path,
        input,
        authHeader(token),
        expectedStatus = Response::expectNoContent
    )
}
