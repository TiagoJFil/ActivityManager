package pt.isel.ls

import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import pt.isel.ls.api.getApiRoutes
import pt.isel.ls.api.getAppRoutes
import pt.isel.ls.config.EnvironmentType
import pt.isel.ls.config.getEnv

fun main() {

    val env = EnvironmentType.TEST.getEnv()

    val api = getApiRoutes(
        getAppRoutes(env)
    )

    with(server(api, env.serverPort)) {
        start()
        readln()
        stop()
    }
}

fun server(api: RoutingHttpHandler, port: Int): Http4kServer =
    api.asServer(Jetty(port)).start()
