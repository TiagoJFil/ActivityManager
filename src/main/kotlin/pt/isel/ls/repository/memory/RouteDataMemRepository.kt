package pt.isel.ls.repository.memory

import pt.isel.ls.services.dto.RouteDTO
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.services.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID

class RouteDataMemRepository(testRoute: Route): RouteRepository {

    /**
     * Mapping between the [RouteID] and [Route]
     */
    private val routesMap = mutableMapOf<RouteID, Route>(testRoute.id to testRoute)

    /**
     * Gets all the existing routes.
     *
     * @return [List] of [Route]
     */
    override fun getRoutes(): List<Route> = routesMap.values.toList()

    /**
     * Adds a new route.
     *
     * @param route the route to add
     */
    override fun addRoute(
            routeId: RouteID,
            startLocation: String,
            endLocation: String,
            distance: Double,
            userID: UserID
    ){
        val route = Route(routeId, startLocation, endLocation, distance, userID)
        routesMap[routeId] = route
    }

    /**
     * Gets a route by the given id.
     *
     * @param id the unique identifier of the route to get
     * @return [Route] the route object or null if the id doesn't exist
     */
    override fun getRoute(id: RouteID): Route? = routesMap[id]

}

