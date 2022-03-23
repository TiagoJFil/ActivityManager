package pt.isel.ls

import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.asServer
import org.http4k.server.Jetty
import pt.isel.ls.http.getApiRoutes
import pt.isel.ls.http.getRoutes
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.guestUser


private const val DEFAULT_PORT = 9000

fun main(){

    val userRepo = UserDataMemRepository(guestUser)
    val routeRepo = RouteDataMemRepository()

    val userServices = UserServices(userRepo)
    val routeServices = RouteServices(routeRepo)

    val api = getApiRoutes(
        getRoutes(
            userServices=userServices,
            routeServices=routeServices
        )
    )

    with(server(api, DEFAULT_PORT)){
        start()
        readln()
        stop()
    }

}

fun server(api: RoutingHttpHandler, port: Int): Http4kServer {

    val debugApi = PrintRequestAndResponse().then(api)

    return debugApi.asServer(Jetty(port)).start()
}

