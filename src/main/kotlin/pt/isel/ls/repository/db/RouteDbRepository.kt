package pt.isel.ls.repository.db

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository

typealias RouteID = String

class RouteDbRepository: RouteRepository {
    override fun getRoutes(): List<Route> {
        TODO("Not yet implemented")
    }

    override fun addRoute(newRoute: Route) {
        TODO("Not yet implemented")
    }

    override fun getRoute(id: RouteID): Route? {
        TODO("Not yet implemented")
    }

}