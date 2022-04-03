package pt.isel.ls.repository.database.utils

import pt.isel.ls.services.entities.Sport
import java.sql.ResultSet


fun ResultSet.toSport() = Sport(
    id = getInt("id").toString(),
    name = getString("name"),
    description = getString("description"),
    user = getInt("user").toString()
)
