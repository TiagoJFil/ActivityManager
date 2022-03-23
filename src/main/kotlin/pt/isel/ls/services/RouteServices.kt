package pt.isel.ls.services

import pt.isel.ls.repository.RouteRepository

class RouteServices(val repository: RouteRepository){

    fun getRoutes() = repository.getRoutes()

}
