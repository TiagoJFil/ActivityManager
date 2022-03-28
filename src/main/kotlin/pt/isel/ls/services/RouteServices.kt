package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.utils.*


class RouteServices(
    private val routeRepository: RouteRepository
    ){

    /**
     * Returns a list of all routes.
     * @return a list of all routes.
     */
    fun getRoutes() = routeRepository.getRoutes()

    /**
     * Creates a new route.
     * @param userId the unique id that identifies the user
     * @param startLocation the starting location
     * @param endLocation the end location
     * @param distance the route's distance
     * @return [RouteID] the unique id that identifies the route
     */
    fun createRoute(userId: UserID, startLocation: String?, endLocation: String?, distance: Double?): RouteID{
        val safeStartLocation = requireParameter(startLocation, "startLocation")
        val safeEndLocation = requireParameter(endLocation, "endLocation")
        if(distance == null) throw MissingParameter("distance")

        val routeId = generateRandomId()
        val route = Route(routeId, safeStartLocation, safeEndLocation, distance, userId)

        routeRepository.addRoute(route)

        return routeId
    }

    /**
     * Gets a route by its unique id.
     *
     * @param routeID the unique id that identifies the route
     * @return [Route] the route identified by the given id
     */
    fun getRoute(routeID: String?): Route {
        val safeRouteID = requireParameter(routeID, "routeID")
        return routeRepository.getRoute(safeRouteID) ?: throw ResourceNotFound("Route", "$routeID")
    }

}
