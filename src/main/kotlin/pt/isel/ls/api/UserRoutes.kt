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
import pt.isel.ls.utils.api.json
import pt.isel.ls.utils.api.pagination
import pt.isel.ls.utils.logRequest
import pt.isel.ls.utils.loggerFor

class UserRoutes(
    private val userServices: UserServices
) {
    @Serializable
    data class UserInput(val name: Param = null, val email: Param = null, val password: Param = null)

    @Serializable
    data class UserListOutput(val users: List<UserDTO>)

    @Serializable
    data class AuthInput(val email: Param = null, val password: Param = null)

    @Serializable
    data class AuthOutput(val authToken: UserToken, val id: UserID)

    companion object {
        private val logger = loggerFor<UserRoutes>()
        private const val UID_PLACEHOLDER = "uid"
    }

    /**
     * Creates an [UserDTO] with the information that comes in the body of the HTTP request.
     */
    private fun createUser(request: Request): Response {
        logger.logRequest(request)

        val bodyString = request.bodyString()
        val body = Json.decodeFromString<UserInput>(bodyString)

        val (token, id) = userServices.createUser(body.name, body.email, body.password)

        val authJson = Json.encodeToString(AuthOutput(token, id))

        return Response(CREATED)
            .json(authJson)
    }

    /**
     * Gets the user that is identified by the id that comes in the params of uri's path.
     */
    private fun getUserDetails(request: Request): Response {
        logger.logRequest(request)

        val userId = request.path(UID_PLACEHOLDER)
        val userResponse = userServices.getUserByID(userId)
        val userJson = Json.encodeToString(userResponse)

        return Response(Status.OK)
            .json(userJson)
    }

    /**
     * Get the list of [User] that have an activity with the given sport and rid.
     */
    private fun getUsersByActivity(request: Request): Response {
        logger.logRequest(request)
        val sportID = request.path("sid")
        val routeID = request.query("rid")

        val users = userServices.getUsersByActivity(sportID, routeID, request.pagination)
        val userListJson = Json.encodeToString(UserListOutput(users))

        return Response(Status.OK)
            .json(userListJson)
    }

    /**
     * Gets all the users.
     */
    private fun getUsers(request: Request): Response {
        logger.logRequest(request)

        val users = userServices.getUsers(request.pagination)
        val userListJson = Json.encodeToString(UserListOutput(users))

        return Response(Status.OK)
            .json(userListJson)
    }

    /**
     * Gets the token for the user that is identified by the email that comes in the body of the HTTP request.
     */
    private fun authenticate(request: Request): Response {
        logger.logRequest(request)

        val bodyString = request.bodyString()
        val body = Json.decodeFromString<AuthInput>(bodyString)

        val (token, id) = userServices.getUserInfoByAuth(body.email, body.password)

        val authOutput = Json.encodeToString(AuthOutput(token, id))

        return Response(Status.OK)
            .json(authOutput)
    }

    val handler: RoutingHttpHandler = routes(
        "/users" bind routes(
            "/" bind Method.POST to ::createUser,
            "/" bind Method.GET to ::getUsers,
            "/{$UID_PLACEHOLDER}" bind Method.GET to ::getUserDetails,
            // TODO: Trocar o display da activity com o nome do sport e a data da mesma.
        ),
        "/login" bind Method.POST to ::authenticate,
        "/sports/{sid}/users" bind Method.GET to ::getUsersByActivity
    )
}

fun User(userServices: UserServices) =
    UserRoutes(userServices).handler
