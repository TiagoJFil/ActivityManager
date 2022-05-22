package pt.isel.ls.repository

import pt.isel.ls.service.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo

interface RouteRepository {

    /**
     * Returns all the routes stored in the repository.
     */
    fun getRoutes(paginationInfo: PaginationInfo, startLocationQuery: String?, endLocationQuery: String?): List<Route>

    /**
     * Adds a new route to the repository.
     * @param startLocation The start location of the route.
     * @param endLocation The end location of the route.
     * @param distance The distance of the route.
     * @param userID The id of the user that created the route.
     */
    fun addRoute(startLocation: String, endLocation: String, distance: Float, userID: UserID): RouteID

    /**
     * Returns the route with the given id.
     * @param routeID The id of the route to be returned.
     * @return [Route] The route with the given id.
     */
    fun getRoute(routeID: RouteID): Route?

    /**
     * Verifies if a route with the given id exists in the repository.
     * @param routeID The id of the route to be verified.
     * @return [Boolean] True if the route exists, false otherwise.
     */
    fun hasRoute(routeID: RouteID): Boolean

    /**
     * Updates the route with the given id.
     * @param routeID The id of the route to be updated.
     * @param startLocation The new start location of the route.
     * @param endLocation The new end location of the route.
     * @param distance The new distance of the route.
     */
    fun updateRoute(routeID: RouteID, startLocation: String?, endLocation: String?, distance: Float?): Boolean
}
