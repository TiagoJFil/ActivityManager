package pt.isel.ls.repository.database.utils


import pt.isel.ls.services.entities.Route
import java.sql.ResultSet

fun ResultSet.toRoute(): Route = Route(
        id= getString("id"),
        startLocation= getString("startlocation"),
        endLocation= getString("endlocation"),
        distance= getDouble("distance"),
        user= getString("user")
)

