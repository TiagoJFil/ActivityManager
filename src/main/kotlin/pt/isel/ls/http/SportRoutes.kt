package pt.isel.ls.http

import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.services.SportsServices

class SportRoutes(
    val sportsServices: SportsServices
) {

    private fun createSport(request: Request): Response{
        TODO()
    }

    private fun getSport(request: Request): Response {
        TODO()
    }

    private fun getSports(request: Request): Response {
        TODO()
    }


    val handler = routes(
        "/sports" bind routes(
            "/" bind POST to ::createSport,
            "/{id}" bind GET to ::getSport,
            "/" bind GET to ::getSports,
        )
    )

}

fun sportsRoutes(sportsServices: SportsServices) = SportRoutes(sportsServices).handler