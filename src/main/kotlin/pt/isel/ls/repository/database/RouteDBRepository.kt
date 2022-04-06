package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.database.utils.*
import pt.isel.ls.services.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID
import java.sql.ResultSet
import java.sql.Statement

class RouteDBRepository(private val dataSource: PGSimpleDataSource, val suffix: String) : RouteRepository{
    /**
     * Returns all the routes stored in the repository.
     */
    override fun getRoutes(): List<Route> =
        dataSource.connection.transaction {
            createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT * FROM route")
                rs.toListOf<Route>(ResultSet::toRoute)
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
    ) : RouteID =
        dataSource.connection.transaction {
            val query = """INSERT INTO route(startlocation,endlocation,distance,"user") VALUES (?, ?, ?, ?)"""
            prepareStatement(query, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.apply {
                    setRoute(startLocation, endLocation, distance, userID.toInt())
                    executeUpdate()
                }.generatedKey()
            }
        }

    /**
     * Returns the route with the given id.
     * @param routeID The id of the route to be returned.
     * @return [Route] The route with the given id.
     */
    override fun getRoute(routeID: RouteID): Route? =
        dataSource.connection.transaction {
            prepareStatement(
                    """SELECT * FROM route WHERE id = ?"""
            ).use { statement ->
                statement.setInt(1, routeID.toInt())
                statement.executeQuery().use { resultSet ->
                    resultSet.ifNext { resultSet.toRoute() }
                }
            }
        }



    /**
     * Verifies if a route with the given id exists in the repository.
     * @param routeID The id of the route to be verified.
     * @return [Boolean] True if the route exists, false otherwise.
     */
    override fun hasRoute(routeID: RouteID): Boolean =
        dataSource.connection.transaction {
            prepareStatement(
                    """SELECT * FROM route WHERE id = ?"""
            ).use { statement ->
                statement.setInt(1, routeID.toInt())
                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                }
            }
        }

}
