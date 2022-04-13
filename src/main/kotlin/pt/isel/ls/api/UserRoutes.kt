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
import pt.isel.ls.service.UserServices
import pt.isel.ls.service.dto.UserDTO
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.infoLogRequest

class UserRoutes(
    private val userServices: UserServices
) {

    @Serializable data class UserCreationInput(val name: Param = null, val email: Param = null)
    @Serializable data class UserIDOutput(val authToken: UserToken, val id: UserID)
    @Serializable data class UserListOutput(val users: List<UserDTO>)

    companion object {
        val logger = getLoggerFor<UserRoutes>()
    }

    /**
     * Creates an [UserDTO] with the information that comes in the body of the HTTP request.
     */
    private fun createUser(request: Request): Response {
        logger.infoLogRequest(request)

        val bodyString = request.bodyString()
        val body = Json.decodeFromString<UserCreationInput>(bodyString)

        val res = userServices.createUser(body.name, body.email)

        return Response(CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(UserIDOutput(res.first, res.second)))
    }

    /**
     * Gets the user that is identified by the id that comes in the params of uri's path.
     */
    private fun getUserDetails(request: Request): Response {
        logger.infoLogRequest(request)

        val userId = request.path("uid")
        val userResponse = userServices.getUserByID(userId)
        val userEncoded = Json.encodeToString(userResponse)
        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(userEncoded)
    }

    /**
     * Gets all the users.
     */
    private fun getUsers(request: Request): Response {
        logger.infoLogRequest(request)

        val users = userServices.getUsers()
        val usersJsonString = Json.encodeToString(UserListOutput(users))

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(usersJsonString)
    }

    val handler: RoutingHttpHandler =
        "/users" bind routes(
            "/" bind Method.POST to ::createUser,
            "/" bind Method.GET to ::getUsers,
            "/{uid}" bind Method.GET to ::getUserDetails,
        )
}

fun User(userServices: UserServices) =
    UserRoutes(userServices).handler
