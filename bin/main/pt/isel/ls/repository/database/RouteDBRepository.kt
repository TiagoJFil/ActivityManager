package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.service.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.repository.generatedKey
import pt.isel.ls.utils.repository.ifNext
import pt.isel.ls.utils.repository.setRoute
import pt.isel.ls.utils.repository.toListOf
import pt.isel.ls.utils.repository.toRoute
import pt.isel.ls.utils.repository.transaction
import java.sql.ResultSet
import java.sql.Statement

class RouteDBRepository(private val dataSource: PGSimpleDataSource, suffix: String) : RouteRepository {

    private val routeTable = "route$suffix"

    /**
     * Returns all the routes stored in the repository.
     */
    override fun getRoutes(): List<Route> =
        dataSource.connection.transaction {
            createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT * FROM $routeTable")
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
    ): RouteID =
        dataSource.connection.transaction {
            val query = """INSERT INTO  $routeTable(startlocation,endlocation,distance,"user") VALUES (?, ?, ?, ?)"""
            prepareStatement(query, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.apply {
                    setRoute(startLocation, endLocation, distance, userID)
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
        queryRouteByID(routeID) { resultSet ->
            resultSet.ifNext { resultSet.toRoute() }
        }

    /**
     * Verifies if a route with the given id exists in the repository.
     * @param routeID The id of the route to be verified.
     * @return [Boolean] True if the route exists, false otherwise.
     */
    override fun hasRoute(routeID: RouteID): Boolean =
        queryRouteByID(routeID) { rs ->
            rs.next()
        }

    /**
     * Makes a query to get a route by its identifier.
     *
     * @param routeID The id of the route to be queried.
     * @param block specifies what the caller wants to do with the result set.
     * @return [T] The result of calling the block function.
     */
    private fun <T> queryRouteByID(routeID: RouteID, block: (ResultSet) -> T): T =
        dataSource.connection.transaction {
            val query = """SELECT * FROM $routeTable WHERE id = ?"""
            val pstmt = prepareStatement(query)
            pstmt.use { ps ->
                ps.setInt(1, routeID)
                val resultSet: ResultSet = ps.executeQuery()
                resultSet.use { block(it) }
            }
        }
}
