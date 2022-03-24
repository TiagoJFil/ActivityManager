package pt.isel.ls.http

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices


class ActivityRoutes(
    val activityServices: ActivityServices,
    val sportsServices: SportsServices,
    val userServices: UserServices
){

    private fun createActivity(request: Request): Response {
        TODO()
    }



    val handler =
        "/activity" bind
                routes(
                    "/{sid}" bind Method.POST to { Response(Status.NOT_IMPLEMENTED) },
                )

}

fun activityRoutes( activityServices: ActivityServices, sportsServices: SportsServices, userServices: UserServices) =
    ActivityRoutes(activityServices,sportsServices,userServices).handler
