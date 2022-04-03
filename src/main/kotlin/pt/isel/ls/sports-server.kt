package pt.isel.ls

import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import pt.isel.ls.api.getApiRoutes
import pt.isel.ls.api.getAppRoutes
import pt.isel.ls.utils.EnvironmentType
import pt.isel.ls.utils.getEnv

private const val DEFAULT_PORT = 9000


fun main() {

    val api = getApiRoutes(
        getAppRoutes(EnvironmentType.TEST.getEnv())
    )

    with(server(api, DEFAULT_PORT)) {
        start()
        readln()
        stop()
    }

}

fun server(api: RoutingHttpHandler, port: Int): Http4kServer
     = api.asServer(Jetty(port)).start()

