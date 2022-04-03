package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.database.utils.toRoute
import pt.isel.ls.repository.database.utils.transaction
import pt.isel.ls.services.entities.Route
import pt.isel.ls.services.entities.User
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.guestUser
import pt.isel.ls.utils.testRoute
import java.sql.Statement


class RouteDBRepository(private val dataSource: PGSimpleDataSource) : RouteRepository{
    /**
     * Returns all the routes stored in the repository.
     */
    override fun getRoutes(): List<Route> {
        dataSource.connection.use { connection ->
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM route")
            val routes = mutableListOf<Route>()
            while (resultSet.next()) {
                routes.add(resultSet.toRoute())
            }
            return routes
        }

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

        dataSource.connection.use { connection ->
            return connection.transaction {
                val routeID : RouteID
                connection.prepareStatement(
                        """INSERT INTO route(id,startlocation,endlocation,distance,"user") VALUES (DEFAULT,?, ?, ?, ?)""",
                        Statement.RETURN_GENERATED_KEYS

                ).use { statement ->
                    statement.setString(1, startLocation)
                    statement.setString(2, endLocation)
                    statement.setDouble(3, distance)
                    statement.setInt(4, userID.toInt())
                    statement.executeUpdate()
                    statement.generatedKeys.use {
                        it.next()
                        routeID = it.getInt(1).toString()
                    }
                    return@transaction routeID
                }
            }
        }
    }

    /**
     * Returns the route with the given id.
     * @param routeID The id of the route to be returned.
     * @return [Route] The route with the given id.
     */
    override fun getRoute(routeID: RouteID): Route? {
        dataSource.connection.use { connection ->
            connection.transaction {
                connection.prepareStatement(
                        """SELECT * FROM route WHERE id = ?"""
                ).use { statement ->
                    statement.setInt(1, routeID.toInt())
                    statement.executeQuery().use { resultSet ->
                        if(resultSet.next()) {
                            return resultSet.toRoute()
                        }
                        return null
                    }
                }
            }
        }
    }

    /**
     * Verifies if a route with the given id exists in the repository.
     * @param routeID The id of the route to be verified.
     * @return [Boolean] True if the route exists, false otherwise.
     */
    override fun hasRoute(routeID: RouteID): Boolean {
        dataSource.connection.use { connection ->
            connection.transaction {
                connection.prepareStatement(
                        """SELECT * FROM route WHERE id = ?"""
                ).use { statement ->
                    statement.setString(1, routeID)
                    statement.executeQuery().use { resultSet ->
                        return resultSet.next()
                    }
                }
            }
        }
    }

}
