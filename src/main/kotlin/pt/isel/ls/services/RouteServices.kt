package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository

class RouteServices(val repository: RouteRepository){

    fun getRoutes() = repository.getRoutes()

    fun getRoute(routeID: String?) : Route? {
        requireNotNull(routeID) {" id must not be null"}
        require(routeID.isNotBlank()) {" id field has no value "}
        val route = getRoute(routeID)
        checkNotNull(route){" No route found by the id given"}
        return route
    }

}
