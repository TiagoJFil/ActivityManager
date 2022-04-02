package pt.isel.ls.services

import pt.isel.ls.services.dto.RouteDTO
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.entities.Route
import pt.isel.ls.utils.*


class RouteServices(
    private val routeRepository: RouteRepository,
    private val userRepository: UserRepository
    ){
    companion object{
        val logger = getLoggerFor<UserServices>()
    }

    /**
     * Returns a list of all routes.
     * @return a list of all routes.
     */
    fun getRoutes() = routeRepository
            .getRoutes()
            .map(Route::toDTO)

    /**
     * Creates a new route.
     * @param token the user token to be used to verify the user.
     * @param startLocation the starting location
     * @param endLocation the end location
     * @param distance the route's distance
     * @return [RouteID] the unique id that identifies the route
     */
    fun createRoute(token: UserToken?, startLocation: String?, endLocation: String?, distance: Double?): RouteID{
        logger.traceFunction("createRoute","startLocation: $startLocation","endLocation: $endLocation","distance: $distance")
        val userID = userRepository.requireAuthenticated(token)

        val safeStartLocation = requireParameter(startLocation, "startLocation")
        val safeEndLocation = requireParameter(endLocation, "endLocation")
        if(distance == null) throw MissingParameter("distance")

        val routeId = generateRandomId()

        routeRepository.addRoute(routeId, safeStartLocation, safeEndLocation, distance, userID)

        return routeId
    }

    /**
     * Gets a route by its unique id.
     *
     * @param routeID the unique id that identifies the route
     * @return [RouteDTO] the route identified by the given id
     */
    fun getRoute(routeID: String?): RouteDTO {
        logger.traceFunction("getRoute","routeID: $routeID")

        val safeRouteID = requireParameter(routeID, "routeID")
        return routeRepository.getRoute(safeRouteID)?.toDTO()
                ?: throw ResourceNotFound("Route", "$routeID")
    }

}
