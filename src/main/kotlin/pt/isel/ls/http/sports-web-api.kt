package pt.isel.ls.http



import org.http4k.core.*
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.MOVED_PERMANENTLY
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes


//filter do http4k para os erros


fun createUser(request: Request): Response {
    val name = request.body("name")
    val email = request.body("email")

    TODO("verify types")



    //createUser()
    return Response(CREATED)
}

fun getUserDetails(request: Request): Response {
    val id = request.query("id") ?: error("TBD")

    return Response(OK)
}

fun getUsers(request: Request): Response {

    return Response(OK)
}


private val userRoutes = routes(
    "/" bind Method.POST to ::createUser,
    "/" bind Method.GET to ::getUsers,
    "/{id}" bind Method.GET to ::getUserDetails,
)
private val routeRoutes = routes(
    "/" bind Method.POST to {Response(MOVED_PERMANENTLY)},
    "/" bind Method.GET to {Response(MOVED_PERMANENTLY)},
    "/{id}" bind Method.GET to {Response(MOVED_PERMANENTLY)},
)

private fun getRoutes() = routes(
    "/users" bind userRoutes,
    "/routes" bind routeRoutes,
)

fun getApiRoutes() = routes(
    "/api" bind getRoutes()
)




