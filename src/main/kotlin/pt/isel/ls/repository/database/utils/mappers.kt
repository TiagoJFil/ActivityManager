package pt.isel.ls.repository.database.utils

import pt.isel.ls.services.entities.Route
import java.sql.ResultSet
import pt.isel.ls.services.entities.Sport

fun ResultSet.toRoute(): Route = Route(
        id= getString("id"),
        startLocation= getString("startlocation"),
        endLocation= getString("endlocation"),
        distance= getDouble("distance"),
        user= getString("user")
)


fun ResultSet.toSport() = Sport(
    id = getInt("id").toString(),
    name = getString("name"),
    description = getString("description"),
    user = getInt("user").toString()
)
