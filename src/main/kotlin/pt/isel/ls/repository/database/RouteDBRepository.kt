package pt.isel.ls.repository.database

import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.services.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID


class RouteDBRepository : RouteRepository{
    /**
     * Returns all the routes stored in the repository.
     */
    override fun getRoutes(): List<Route> {
        TODO("Not yet implemented")
    }

    /**
     * Adds a new route to the repository.
     * @param routeId The id of the route to be added.
     * @param startLocation The start location of the route.
     * @param endLocation The end location of the route.
     * @param distance The distance of the route.
     * @param userID The id of the user that created the route.
     */
    override fun addRoute(
        routeId: RouteID,
        startLocation: String,
        endLocation: String,
        distance: Double,
        userID: UserID
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Returns the route with the given id.
     * @param routeID The id of the route to be returned.
     * @return [Route] The route with the given id.
     */
    override fun getRoute(routeID: RouteID): Route? {
        TODO("Not yet implemented")
    }

    /**
     * Verifies if a route with the given id exists in the repository.
     * @param routeID The id of the route to be verified.
     * @return [Boolean] True if the route exists, false otherwise.
     */
    override fun hasRoute(routeID: RouteID): Boolean {
        TODO("Not yet implemented")
    }

}