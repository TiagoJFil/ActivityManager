package pt.isel.ls.service

import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.dto.RouteDTO
import pt.isel.ls.service.entities.Route
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction

class RouteServices(
    private val routeRepository: RouteRepository,
    private val userRepository: UserRepository
) {
    companion object {
        val logger = getLoggerFor<RouteServices>()
        const val ROUTE_ID_PARAM = "routeID"
        const val START_LOCATION_PARAM = "startLocation"
        const val END_LOCATION_PARAM = "endLocation"
        const val DISTANCE_PARAM = "distance"
        const val RESOURCE_NAME = "Route"
    }

    /**
     * Returns a list of all routes in the repository.
     */
    fun getRoutes(paginationInfo: PaginationInfo) = routeRepository
        .getRoutes(paginationInfo)
        .map(Route::toDTO)

    /**
     * Creates a new route.
     * @param token the user token to be used to verify the user.
     * @param startLocation the starting location
     * @param endLocation the end location
     * @param distance the route's distance
     * @return [RouteID] the unique id that identifies the route
     */
    fun createRoute(token: UserToken?, startLocation: String?, endLocation: String?, distance: Double?): RouteID {
        logger.traceFunction(::createRoute.name) {
            listOf(
                START_LOCATION_PARAM to startLocation,
                END_LOCATION_PARAM to endLocation,
                DISTANCE_PARAM to distance.toString()
            )
        }

        val userID = userRepository.requireAuthenticated(token)

        val safeStartLocation = requireParameter(startLocation, START_LOCATION_PARAM)
        val safeEndLocation = requireParameter(endLocation, END_LOCATION_PARAM)
        if (distance == null) throw MissingParameter(DISTANCE_PARAM)

        return routeRepository.addRoute(safeStartLocation, safeEndLocation, distance, userID)
    }

    /**
     * Gets a route by its unique id.
     *
     * @param rid the unique id that identifies the route
     * @return [RouteDTO] the route identified by the given id
     */
    fun getRoute(rid: Param): RouteDTO {
        logger.traceFunction(::getRoute.name) { listOf(ROUTE_ID_PARAM to rid) }

        val safeRouteID = requireParameter(rid, ROUTE_ID_PARAM)
        val ridInt: RouteID = requireIdInteger(safeRouteID, ROUTE_ID_PARAM)

        return routeRepository.getRoute(ridInt)?.toDTO()
            ?: throw ResourceNotFound(RESOURCE_NAME, "$rid")
    }
}
