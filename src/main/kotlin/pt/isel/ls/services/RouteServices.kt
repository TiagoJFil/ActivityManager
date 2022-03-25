package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.utils.*


class RouteServices(val repository: RouteRepository){

     /**
     * Calls the function [RouteRepository] to get all the routes.
     * @return [List] of [Route]
     */
    fun getRoutes() = repository.getRoutes()

    /**
     * Verifies the parameters received and calls the function [RouteRepository] to create a [Route].
     * @param userId the unique id that identifies the user
     * @param startLocation the starting location
     * @param endLocation the end location
     * @param distance the route's distance
     * @return [RouteID] the unique id that identifies the route
     */
    fun createRoute(userId: UserID, startLocation: String?, endLocation: String?, distance: Double?): RouteID{

        if(startLocation == null) throw MissingParameter("startLocation")
        if(startLocation.isBlank()) throw InvalidParameter("startLocation")

        if(endLocation == null) throw MissingParameter("endLocation")
        if(endLocation.isBlank())  throw InvalidParameter("endLocation")

        if(distance == null) throw MissingParameter("distance")

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
     */
    fun getRoute(routeID: String?): Route {
        if (routeID == null) throw MissingParameter("routeID")
        if (routeID.isBlank()) throw InvalidParameter("routeID")
        return repository.getRoute(routeID) ?: throw ResourceNotFound("Route", "$routeID")
    }

}
