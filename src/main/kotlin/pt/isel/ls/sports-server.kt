package pt.isel.ls

import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import pt.isel.ls.api.getApiRoutes
import pt.isel.ls.api.getAppRoutes
import pt.isel.ls.utils.EnvironmentType

private const val DEFAULT_PORT = 9000


fun main() {

    val api = getApiRoutes(
        getAppRoutes(EnvironmentType.TEST.environment)
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
