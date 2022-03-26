package pt.isel.ls.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.CREATED
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.entities.Activity
import pt.isel.ls.entities.User
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.UserServices


class UserRoutes(
    val userServices: UserServices,
    val activityServices: ActivityServices
){
    /**
     * Creates an [User] with the information that comes in the body of the HTTP request.
     */

    @Serializable data class UserIDResponse(val authToken: String, val id: String)
    @Serializable data class UserCreationBody(val name: String? = null, val email: String? = null)
    private fun createUser(request: Request): Response {
        val bodyString = request.bodyString()
        val body = Json.decodeFromString<UserCreationBody>(bodyString)

        val res = userServices.createUser(body.name, body.email)

        return Response(CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(UserIDResponse(res.first, res.second)))
    }

    /**
     * Gets the user that is identified by the id that comes in the params of uri's path.
     */
    private fun getUserDetails(request: Request): Response {
        val userId = request.path("id")
        val userResponse = userServices.getUserByID(userId)
        val userEncoded = Json.encodeToString(userResponse)
        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(userEncoded)

    }

    @Serializable data class UserList(val users: List<User>)

    /**
     * Gets all the users.
     */
    private fun getUsers(request: Request): Response{
        val users = userServices.getUsers()
        val usersJsonString = Json.encodeToString(UserList(users))

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(usersJsonString)
    }

    @Serializable
    data class ListActivities(val activities: List<Activity>)

    /**
     * Gets all the activities created by the user that matches the given id.
     */
    private fun getActivitiesByUser(request: Request): Response{
        val userId = request.path("id")

        val userResponse = userServices.getUserByID(userId)
        val activities = activityServices.getActivitiesByUser(userResponse.id)
        val activitiesJson = Json.encodeToString(ListActivities(activities))

        return Response(Status.OK)
                .header("content-type", "application/json")
                .body(activitiesJson)
    }

    val handler: RoutingHttpHandler =
        "/users" bind routes(
            "/"     bind  Method.POST to ::createUser,
            "/"     bind Method.GET to ::getUsers,
            "/{id}" bind Method.GET to ::getUserDetails,
            "/{id}/activities" bind Method.GET to ::getActivitiesByUser
        )
}


fun User(userServices: UserServices, activityServices: ActivityServices)
    = UserRoutes(userServices, activityServices).handler





