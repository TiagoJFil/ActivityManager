package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteID
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.UserID


const val START_LOCATION_REQUIRED = "Start location required"
const val END_LOCATION_REQUIRED = "End location required"
const val DISTANCE_REQUIRED = "Distance required"
const val ID_EMPTY = " id field has no value "
const val NO_ROUTE_FOUND = " No route found by the id given"



class RouteServices(val repository: RouteRepository){

     /**
     * Calls the function [RouteRepository] to get all the routes.
     * @return [List] of [Route]
     */
    fun getRoutes() = repository.getRoutes()

    /**
     * Verifies the parameters received and creates calls the function [RouteRepository] to create a [Route].
     * @param userId the unique id that identifies the user
     * @param startLocation the starting location
     * @param endLocation the end location
     * @param distance the route's distance
     * @return [RouteID] the unique id that identifies the route
     * @throws IllegalArgumentException
     */
    fun createRoute(userId: UserID, startLocation: String?, endLocation: String?, distance: Double?): RouteID{
        require(startLocation != null && startLocation.isNotBlank()){ START_LOCATION_REQUIRED}
        require(endLocation != null && endLocation.isNotBlank()){ END_LOCATION_REQUIRED}
        requireNotNull(distance) { DISTANCE_REQUIRED}

        val routeId = generateRandomId()
        val route = Route(routeId, startLocation, endLocation, distance, userId)

        repository.addRoute(route)

        return routeId
    }

    /**
     * Verifies the parameters received and calls the function [RouteRepository] to get a [Route].
     *
     * @param routeID the unique id that identifies the route
     * @return [Route] the route identified by the given id
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    fun getRoute(routeID: String?) : Route {
        requireNotNull(routeID) { ID_REQUIRED }
        require(routeID.isNotBlank()) { ID_EMPTY }
        val route = repository.getRoute(routeID)
        checkNotNull(route){NO_ROUTE_FOUND}
        return route
    }

}
