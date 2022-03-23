package pt.isel.ls.repository

import pt.isel.ls.entities.Route
import pt.isel.ls.repository.memory.RouteID

interface RouteRepository {


    fun getRoutes(): List<Route>

    fun addRoute(newRoute: Route)
}