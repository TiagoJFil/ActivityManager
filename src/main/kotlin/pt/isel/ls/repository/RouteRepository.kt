package pt.isel.ls.repository

import pt.isel.ls.entities.Route


typealias RouteID = String

interface RouteRepository {

    fun getRoutes(): List<Route>

    fun addRoute(newRoute: Route)

    fun getRoute(id: RouteID) : Route?
}