package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.memory.RouteID
import pt.isel.ls.repository.memory.UserID

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
        val route = Route(userId, startLocation, endLocation, distance, routeId)

        repository.addRoute(route)

        return routeId
    }
}
