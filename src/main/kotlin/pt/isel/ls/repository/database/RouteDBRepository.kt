package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.services.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID


class RouteDBRepository(private val dataSource: PGSimpleDataSource) : RouteRepository{
    /**
     * Returns all the routes stored in the repository.
     */
    override fun getRoutes(): List<Route> {
        TODO("Not yet implemented")
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
    ) : RouteID{
        val routeID : RouteID
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """INSERT INTO route(id,startlocation,endlocation,distance,"user") VALUES (DEFAULT,?, ?, ?, ?)"""

            ).use { statement ->
                statement.setString(1, startLocation)
                statement.setString(2, endLocation)
                statement.setDouble(3, distance)
                statement.setString(4, userID)
                statement.executeUpdate()
                statement.generatedKeys.use {
                    it.next()
                    routeID = it.getInt(1).toString()
                }
            }

        }
        return routeID
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