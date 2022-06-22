package pt.isel.ls.service

import pt.isel.ls.service.dto.RouteDTO
import pt.isel.ls.service.entities.Route
import pt.isel.ls.service.inputs.ActivityInputs.validateID
import pt.isel.ls.service.inputs.RouteInputs.RouteCreateInput
import pt.isel.ls.service.inputs.RouteInputs.RouteUpdateInput
import pt.isel.ls.service.transactions.TransactionFactory
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.loggerFor
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireOwnership
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
     * @param route [RouteCreateInput] data given by the user creating the route.
     * @return [RouteID] the unique id that identifies the route
     */
    fun createRoute(token: UserToken?, route: RouteCreateInput): RouteID {
        val startLocation = route.startLocation
        val endLocation = route.endLocation
        val distance = route.distance

        logger.traceFunction(::createRoute.name) {
            listOf(
                START_LOCATION_PARAM to startLocation,
                END_LOCATION_PARAM to endLocation,
                DISTANCE_PARAM to distance.toString()
            )
        }
        return transactionFactory.getTransaction().execute {
            val userID = usersRepository.requireAuthenticated(token)

            routesRepository.addRoute(startLocation, endLocation, distance, userID)
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

        val routeID = validateID(rid, ROUTE_ID_PARAM)

        return transactionFactory.getTransaction().execute {
            routesRepository.getRoute(routeID)?.toDTO()
                ?: throw ResourceNotFound(RESOURCE_NAME, "$routeID")
        }
    }

    fun updateRoute(token: UserToken?, route: RouteUpdateInput) {
        val routeID = route.routeID
        val startLocation = route.startLocation
        val endLocation = route.endLocation
        val distance = route.distance

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
            routesRepository.requireOwnership(userID, routeID)

            if (route.hasNothingToUpdate) return@execute
            // No update needed, don't waste resources

            if (!routesRepository.updateRoute(routeID, startLocation, endLocation, distance))
                throw ResourceNotFound(RESOURCE_NAME, routeID.toString())
        }
    }
}
