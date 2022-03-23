package pt.isel.ls.repository

import pt.isel.ls.entities.Route

interface RouteRepository {


    fun getRoutes(): List<Route>

}