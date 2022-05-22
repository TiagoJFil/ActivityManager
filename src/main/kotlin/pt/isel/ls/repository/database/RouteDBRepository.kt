package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.service.entities.Route
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.repository.applyPagination
import pt.isel.ls.utils.repository.generatedKey
import pt.isel.ls.utils.repository.ifNext
import pt.isel.ls.utils.repository.queryTableByID
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
    override fun getRoutes(paginationInfo: PaginationInfo, startLocationQuery: String?, endLocationQuery: String?): List<Route> =
        dataSource.connection.transaction {
            val query = buildSearchQuery(startLocationQuery, endLocationQuery)

            prepareStatement(query).use { stmt ->
                stmt.applyPagination(paginationInfo, indexes = Pair(1, 2))
                val rs = stmt.executeQuery()
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
        distance: Float,
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
        distance: Float?
    ): Boolean {
        val queryBuilder = StringBuilder("UPDATE $routeTable SET ")
        if (startLocation != null) queryBuilder.append("startlocation = ?, ")
        if (endLocation != null) queryBuilder.append("endlocation = ?, ")
        if (distance != null) queryBuilder.append("distance = ? ")
        queryBuilder.append("WHERE id = ?")

        val startLocationIdx = if (startLocation != null) 1 else 0
        val endLocationIdx = if (endLocation != null) startLocationIdx + 1 else startLocationIdx
        val distanceIdx = if (distance != null) endLocationIdx + 1 else endLocationIdx
        println()
        dataSource.connection.transaction {
            prepareStatement(queryBuilder.toString()).use { stmt ->
                if (startLocation != null) stmt.setString(startLocationIdx, startLocation)
                if (endLocation != null) stmt.setString(endLocationIdx, endLocation)
                if (distance != null) stmt.setFloat(distanceIdx, distance)
                stmt.setInt(distanceIdx + 1, routeID)
                return stmt.executeUpdate() == 1
            }
        }
    }

    /**
     * Auxiliary function to build the query to the get Routes function
     */
    private fun buildSearchQuery(startLocationQuery: String?, endLocationQuery: String?): String {
        val query = StringBuilder("SELECT * FROM $routeTable")
        if (startLocationQuery != null || endLocationQuery != null)
            query.append(" WHERE ")

        if (startLocationQuery != null)
            query.append("startlocation LIKE '%${startLocationQuery.lowercase()}%'")

        if (startLocationQuery != null && endLocationQuery != null)
            query.append(" AND ")

        if (endLocationQuery != null)
            query.append("endlocation LIKE '%${endLocationQuery.lowercase()}%'")

        query.append(" LIMIT ? OFFSET ?")
        return query.toString()
    }

    /**
     * Makes a query to get a route by its identifier.
     *
     * @param routeID The id of the route to be queried.
     * @param block specifies what the caller wants to do with the result set.
     * @return [T] The result of calling the block function.
     */
    private fun <T> queryRouteByID(routeID: RouteID, block: (ResultSet) -> T): T =
        dataSource.queryTableByID(routeID, routeTable, block)
}
