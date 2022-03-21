package pt.isel.ls.http

import org.http4k.routing.bind
import org.http4k.server.asServer
import org.http4k.server.Jetty


fun main() {
    val jettyServer = getApiRoutes().asServer(Jetty(9000)).start()

    readln()
    jettyServer.stop()
}
