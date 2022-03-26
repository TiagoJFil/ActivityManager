package pt.isel.ls

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import pt.isel.ls.api.getApiRoutes
import pt.isel.ls.api.getAppRoutes
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.guestUser
import java.util.logging.Handler

private const val DEFAULT_PORT = 9000

fun main() {

    val userRepo = UserDataMemRepository(guestUser)
    val userServices = UserServices(userRepo)

    val routeRepo = RouteDataMemRepository()
    val routeServices = RouteServices(routeRepo)

    val sportsRepo = SportDataMemRepository()
    val sportsServices = SportsServices(sportsRepo)

    val activityRepo = ActivityDataMemRepository()
    val activityServices = ActivityServices(activityRepo)

    val api = getApiRoutes(
        getAppRoutes(
            userServices = userServices,
            routeServices = routeServices,
            sportsServices = sportsServices,
            activityServices = activityServices
        )
    )

    with(server(api, DEFAULT_PORT)) {
        start()
        readln()
        stop()
    }

}

fun server(api: RoutingHttpHandler, port: Int): Http4kServer {

    val debugApi = PrintRequestAndResponse().then(api)

    return debugApi.asServer(Jetty(port)).start()
}
