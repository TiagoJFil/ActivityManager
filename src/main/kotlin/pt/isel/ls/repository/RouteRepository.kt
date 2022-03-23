package pt.isel.ls.repository

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.memory.RouteID
import pt.isel.ls.entities.RouteID

interface RouteRepository {

    fun getRoutes(): List<Route>

    fun addRoute(newRoute: Route)

    fun getRoute(id: RouteID) : Route ?
}