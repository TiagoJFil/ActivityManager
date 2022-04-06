package pt.isel.ls.repository.database.utils

import kotlinx.datetime.toLocalDate
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Activity.Duration
import pt.isel.ls.services.entities.Route
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.entities.User.Email
import java.sql.ResultSet

fun ResultSet.toRoute(): Route = Route(
    id = getString("id"),
    startLocation = getString("startlocation"),
    endLocation = getString("endlocation"),
    distance = getDouble("distance"),
    user = getString("user")
)


fun ResultSet.toSport() = Sport(
    id = getInt("id").toString(),
    name = getString("name"),
    description = getString("description"),
    user = getInt("user").toString()
)


fun ResultSet.toUser(email: Email) = User(
    id = getInt("id").toString(),
    name = getString("name"),
    email = email,
)

fun ResultSet.toActivity() = Activity(
    id = getString("id"),
    date = getString("date").toLocalDate(),
    duration = Duration(getLong("duration")),
    sport = getInt("sport").toString(),
    route = getInt("route").toString(),
    user = getInt("user").toString()
)

/**
 * Converts a ResultSet to a list of [T]s.
 * @param mapper a function that converts a ResultSet to a [T]
 */
fun <T> ResultSet.toListOf(mapper: ResultSet.() -> T): List<T> =
    use {
        val list = mutableListOf<T>()
        while (next()) {
            list.add(mapper())
        }
        return list
    }


