package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository

class RouteDataMemRepository: RouteRepository {

    private val routesMap = mutableMapOf<RouteID, Route>()

    override fun getRoutes(): List<Route> = routesMap.values.toList()

    override fun addRoute(newRoute: Route){
        routesMap[newRoute.id] = newRoute
    }

    override fun getRoute(id: RouteID): Route? = routesMap[id]
}

typealias RouteID = String