package pt.isel.ls.api.utils

import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import pt.isel.ls.api.ActivityRoutes.ActivityCreation
import pt.isel.ls.api.ActivityRoutes.ActivityIdResponse
import pt.isel.ls.api.RouteRoutes
import pt.isel.ls.api.RouteRoutes.RouteIDResponse
import pt.isel.ls.api.SportRoutes.SportCreationBody
import pt.isel.ls.api.SportRoutes.SportIDResponse
import pt.isel.ls.api.UserRoutes.*
import pt.isel.ls.utils.GUEST_TOKEN
import pt.isel.ls.utils.SportID

const val ROUTE_PATH = "/api/routes/"
const val USER_PATH = "/api/users/"
const val ACTIVITY_PATH = "/api/activity/"
const val SPORT_PATH = "/api/sports/"

/**
 * Auth header
 */
fun authHeader(token: String) = listOf("Authorization" to  "Bearer $token")

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
 * Helper function to create an activity, ensures it is created and returns the respective [ListActivities]
 * @param activityCreationBody the body of the activity to be created. Must be valid.
 */
fun RoutingHttpHandler.createActivity(activityCreationBody: ActivityCreation, sportID: SportID): ActivityIdResponse
        = postRequest(
        this,
        "$ACTIVITY_PATH$sportID",
        activityCreationBody,
        headers = authHeader(GUEST_TOKEN),
        Response::expectCreated
)

/**
 * Helper function to create a sport, ensures it is created and returns the respective [SportIDResponse]
 * @param sportCreationBody the body of the sport to be created. Must be valid.
 */
fun RoutingHttpHandler.createSport(sportCreationBody: SportCreationBody): SportIDResponse
        = postRequest(
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
fun RoutingHttpHandler.createRoute(routeCreation: RouteRoutes.RouteCreation): RouteIDResponse
        = postRequest<RouteRoutes.RouteCreation, RouteIDResponse>(
        this,
        ROUTE_PATH,
        routeCreation,
        authHeader(GUEST_TOKEN),
        Response::expectCreated
)

