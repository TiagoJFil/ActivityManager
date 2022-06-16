package pt.isel.ls.service

import pt.isel.ls.service.dto.RouteDTO
import pt.isel.ls.service.entities.Route
import pt.isel.ls.service.transactions.TransactionFactory
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.loggerFor
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireNotBlankParameter
import pt.isel.ls.utils.service.requireOwnership
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.requireValidDistance
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction

class RouteServices(
    private val transactionFactory: TransactionFactory
) {
    companion object {
        private val logger = loggerFor<RouteServices>()
        const val ROUTE_ID_PARAM = "routeID"
        const val START_LOCATION_PARAM = "startLocation"
        const val END_LOCATION_PARAM = "endLocation"
        const val DISTANCE_PARAM = "distance"
        const val RESOURCE_NAME = "Route"
        const val START_LOCATION_SEARCH_QUERY = "startlocation_search"
        const val END_LOCATION_SEARCH_QUERY = "endlocation_search"
    }

    /**
     * Returns a list of all routes in the repository.
     */
    fun getRoutes(paginationInfo: PaginationInfo, startLocationQuery: Param, endLocationQuery: Param): List<RouteDTO> {
        logger.traceFunction(::getRoutes.name) {
            listOf(
                START_LOCATION_SEARCH_QUERY to startLocationQuery,
                END_LOCATION_SEARCH_QUERY to endLocationQuery
            )
        }

        val handledStartLocation = startLocationQuery?.ifBlank { null }
        val handledEndLocation = endLocationQuery?.ifBlank { null }

        return transactionFactory.getTransaction().execute {
            val routes = routesRepository.getRoutes(paginationInfo, handledStartLocation, handledEndLocation)
            routes.map(Route::toDTO)
        }
    }

    /**
     * Creates a new route.
     * @param token the user token to be used to verify the user.
     * @param startLocation the starting location
     * @param endLocation the end location
     * @param distance the route's distance
     * @return [RouteID] the unique id that identifies the route
     */
    fun createRoute(token: UserToken?, startLocation: String?, endLocation: String?, distance: Float?): RouteID {
        logger.traceFunction(::createRoute.name) {
            listOf(
                START_LOCATION_PARAM to startLocation,
                END_LOCATION_PARAM to endLocation,
                DISTANCE_PARAM to distance.toString()
            )
        }
        return transactionFactory.getTransaction().execute {

            val userID = usersRepository.requireAuthenticated(token)

            val safeStartLocation = requireParameter(startLocation, START_LOCATION_PARAM)
            val safeEndLocation = requireParameter(endLocation, END_LOCATION_PARAM)
            if (distance == null) throw MissingParameter(DISTANCE_PARAM)
            requireValidDistance(distance, DISTANCE_PARAM)

            routesRepository.addRoute(safeStartLocation, safeEndLocation, distance, userID)
        }
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

        return transactionFactory.getTransaction().execute {
            routesRepository.getRoute(ridInt)?.toDTO()
                ?: throw ResourceNotFound(RESOURCE_NAME, "$rid")
        }
    }

    fun updateRoute(token: UserToken?, routeID: Param, startLocation: Param, endLocation: Param, distance: Float?) {
        logger.traceFunction(::updateRoute.name) {
            listOf(
                ROUTE_ID_PARAM to routeID,
                START_LOCATION_PARAM to startLocation,
                END_LOCATION_PARAM to endLocation,
                DISTANCE_PARAM to distance.toString()
            )
        }

        return transactionFactory.getTransaction().execute {

            val userID = usersRepository.requireAuthenticated(token)
            val safeRouteID = requireParameter(routeID, ROUTE_ID_PARAM)
            val ridInt: RouteID = requireIdInteger(safeRouteID, ROUTE_ID_PARAM)

            routesRepository.requireOwnership(userID, ridInt)

            if (startLocation == null && endLocation == null && distance == null) return@execute
            // No update needed, don't waste resources

            requireNotBlankParameter(startLocation, START_LOCATION_PARAM)
            requireNotBlankParameter(endLocation, END_LOCATION_PARAM)
            requireValidDistance(distance, DISTANCE_PARAM)

            if (!routesRepository.updateRoute(ridInt, startLocation, endLocation, distance))
                throw ResourceNotFound(RESOURCE_NAME, safeRouteID)
        }
    }
}
