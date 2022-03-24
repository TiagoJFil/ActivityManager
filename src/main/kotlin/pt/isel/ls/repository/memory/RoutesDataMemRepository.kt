package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.db.RouteID

class RouteDataMemRepository: RouteRepository {

    private val routesMap = mutableMapOf<RouteID, Route>()

    /**
     * Gets all the existing routes.
     *
     * @return [List] of [Route]
     */
    override fun getRoutes(): List<Route> = routesMap.values.toList()

    /**
     * Adds a new route.
     *
     * @param newRoute the route to add
     */
    override fun addRoute(newRoute: Route){

        routesMap[newRoute.id] = newRoute
    }

    /**
     * Gets a route by the given id.
     *
     * @param id the unique identifier of the route to get
     * @return [Route] the route object or null if the id doesn't exist
     */
    override fun getRoute(id: RouteID): Route? = routesMap[id]

}

