package pt.isel.ls.http

import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.asServer
import org.http4k.server.Jetty


private const val DEFAULT_PORT = 9000

fun main() = server(getApiRoutes(), DEFAULT_PORT)
    .run {
        start()
        readln()
        stop()
        return@run
    }

fun server(api: RoutingHttpHandler, port: Int): Http4kServer {

    val debugApi = PrintRequestAndResponse().then(api)

    return debugApi.asServer(Jetty(port)).start()
}

