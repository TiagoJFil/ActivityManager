package pt.isel.ls.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.entities.User
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.guestUser

class UserRoutes(
    val userServices: UserServices = UserServices(UserDataMemRepository(guestUser))
){
    @Serializable
    data class UserList(val users: List<User>)

    private fun getUserDetails(request: Request): Response {
        val userId = request.path("id")
        val userResponse = userServices.getUserByID(userId)
        val userEncoded = Json.encodeToString(userResponse)
        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(userEncoded)

    }

    private fun getUsers(request: Request): Response{
        val users = userServices.getUsers()
        val usersJsonString = Json.encodeToString(UserList(users))

        return Response(Status.OK)
            .header("content-type", "application/json")
            .body(usersJsonString)
    }

    val handler: RoutingHttpHandler =
        routes(
            "/"     bind  Method.POST to ::getUserDetails,
            "/"     bind Method.GET to ::getUsers,
            "/{id}" bind Method.GET to ::getUserDetails,
        )
}


fun userRoutes() = UserRoutes().handler





