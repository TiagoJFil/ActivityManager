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
    private val routesMap = mutableMapOf<Int, Route>(testRoute.id to testRoute)

    /**
     * Gets all the existing routes.
     *
     * @return [List] of [Route]
     */
    override fun getRoutes(paginationInfo: PaginationInfo): List<Route>
        = routesMap.values.toList().applyPagination(paginationInfo)

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
}
