package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.db.RouteID
import pt.isel.ls.repository.db.UserID


const val START_LOCATION_REQUIRED = "Start location required"
const val END_LOCATION_REQUIRED = "End location required"
const val DISTANCE_REQUIRED = "Distance required"


class RouteServices(val repository: RouteRepository){

    fun getRoutes() = repository.getRoutes()

    fun createRoute(userId: UserID, startLocation: String?, endLocation: String?, distance: Double?): RouteID{
        require(startLocation != null && startLocation.isNotBlank()){ START_LOCATION_REQUIRED}
        require(endLocation != null && endLocation.isNotBlank()){ END_LOCATION_REQUIRED}
        requireNotNull(distance) { DISTANCE_REQUIRED}

        val routeId = generateRandomId()
        val route = Route(routeId, startLocation, endLocation, distance, userId)

        repository.addRoute(route)

        return routeId
    }
    fun getRoute(routeID: String?) : Route? {
        requireNotNull(routeID) {" id must not be null"}
        require(routeID.isNotBlank()) {" id field has no value "}
        val route = repository.getRoute(routeID)
        checkNotNull(route){" No route found by the id given"}
        return route
    }

}
