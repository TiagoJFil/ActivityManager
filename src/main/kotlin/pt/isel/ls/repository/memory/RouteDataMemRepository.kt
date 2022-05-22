package pt.isel.ls.repository.memory

import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.service.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.applyPagination

class RouteDataMemRepository(testRoute: Route) : RouteRepository {

    private var currentID = 0

    /**
     * Mapping between the [RouteID] and [Route]
     */
    private val routesMap = mutableMapOf<RouteID, Route>(testRoute.id to testRoute)

    /**
     * Gets all the existing routes.
     *
     * @return [List] of [Route]
     */
    override fun getRoutes(
        paginationInfo: PaginationInfo,
        startLocationQuery: String?,
        endLocationQuery: String?
    ): List<Route> {
        val routes = routesMap.values.toList()
        return if (startLocationQuery != null && endLocationQuery != null) {
            routes.filter {
                it.startLocation.lowercase().contains(startLocationQuery.lowercase()) && it.endLocation.lowercase()
                    .contains(endLocationQuery.lowercase())
            }.applyPagination(paginationInfo)
        } else if (startLocationQuery != null)
            routes.filter { it.startLocation.lowercase().contains(startLocationQuery.lowercase()) }
                .applyPagination(paginationInfo)
        else if (endLocationQuery != null)
            routes.filter { it.endLocation.lowercase().contains(endLocationQuery.lowercase()) }
                .applyPagination(paginationInfo)
        else
            routes.applyPagination(paginationInfo)
    }

    /**
     * Adds a new route to the repository.
     * @param startLocation The start location of the route.
     * @param endLocation The end location of the route.
     * @param distance The distance of the route.
     * @param userID The id of the user that created the route.
     */
    override fun addRoute(
        startLocation: String,
        endLocation: String,
        distance: Double,
        userID: UserID
    ): RouteID {
        val routeID = ++currentID
        val route = Route(routeID, startLocation, endLocation, distance, userID)
        routesMap[routeID] = route
        return routeID
    }

    /**
     * Gets a route by the given id.
     *
     * @param routeID the unique identifier of the route to get
     * @return [Route] the route object or null if the id doesn't exist
     */
    override fun getRoute(routeID: RouteID): Route? = routesMap[routeID]

    /**
     * Verifies if a route with the given id exists in the repository.
     * @param routeID The id of the route to be verified.
     * @return [Boolean] True if the route exists, false otherwise.
     */
    override fun hasRoute(routeID: RouteID): Boolean =
        routesMap[routeID] != null

    /**
     * Updates the route with the given id.
     * @param routeID The id of the route to be updated.
     * @param startLocation The new start location of the route.
     * @param endLocation The new end location of the route.
     * @param distance The new distance of the route.
     */
    override fun updateRoute(
        routeID: RouteID,
        startLocation: String?,
        endLocation: String?,
        distance: Double?
    ): Boolean {
        val route = routesMap[routeID] ?: return false
        val newRoute = Route(
            routeID,
            startLocation ?: route.startLocation,
            endLocation ?: route.endLocation,
            distance ?: route.distance,
            route.user
        )
        routesMap[routeID] = newRoute
        return true
    }
}
