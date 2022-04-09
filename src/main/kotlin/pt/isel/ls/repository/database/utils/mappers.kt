package pt.isel.ls.repository.database.utils

import kotlinx.datetime.toLocalDate
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.service.entities.Route
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.service.entities.User
import pt.isel.ls.service.entities.User.Email
import java.sql.ResultSet

/**
 * Convert a [ResultSet] to a [Route]
 */
fun ResultSet.toRoute(): Route = Route(
    id = getString("id"),
    startLocation = getString("startlocation"),
    endLocation = getString("endlocation"),
    distance = getDouble("distance"),
    user = getString("user")
)

/**
 * Convert a [ResultSet] to a [Sport]
 */
fun ResultSet.toSport() = Sport(
    id = getInt("id").toString(),
    name = getString("name"),
    description = getString("description"),
    user = getInt("user").toString()
)

/**
 * Convert a [ResultSet] to a [User]
 */
fun ResultSet.toUser(email: Email) = User(
    id = getInt("id").toString(),
    name = getString("name"),
    email = email,
)

/**
 * Convert a [ResultSet] to an [Activity]
 */
fun ResultSet.toActivity() = Activity(
    id = getString("id"),
    date = getString("date").toLocalDate(),
    duration = Duration(getLong("duration")),
    sport = getInt("sport").toString(),
    route = getInt("route").let { if (it == 0) null else it.toString() },
    user = getInt("user").toString()
)

/**
 * Converts a ResultSet to a list of [T]s.
 * @param mapper a function that converts a ResultSet to a [T]
 */
inline fun <T> ResultSet.toListOf(mapper: ResultSet.() -> T): List<T> =
    use {
        val list = mutableListOf<T>()
        while (next()) {
            list.add(mapper())
        }
        return list
    }
