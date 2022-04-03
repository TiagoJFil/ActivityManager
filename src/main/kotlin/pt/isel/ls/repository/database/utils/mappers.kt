package pt.isel.ls.repository.database.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDate
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Activity.Duration
import pt.isel.ls.services.entities.Route
import java.sql.PreparedStatement
import java.sql.ResultSet
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.entities.User.Email
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import java.sql.Date
import java.sql.Types

fun ResultSet.toRoute(): Route = Route(
    id= getString("id"),
    startLocation= getString("startlocation"),
    endLocation= getString("endlocation"),
    distance= getDouble("distance"),
    user= getString("user")
)

fun PreparedStatement.setRoute(startLocation: String, endLocation: String, distance: Double, userID: Int) {
        setString(1, startLocation)
        setString(2, endLocation)
        setDouble(3, distance)
        setInt(4, userID)
}

fun PreparedStatement.setSport(name: String, description: String?, userID: Int) {
        setString(1, name)
        setString(2, description)
        setInt(3, userID)
}

fun PreparedStatement.setActivity(date : LocalDate, duration : Duration, sportID: SportID, routeID: RouteID?, userID: UserID) {
    setDate(1, Date.valueOf(date.toJavaLocalDate()))
    setLong(2, duration.millis)
    setInt(3, sportID.toInt())
    routeID?.let {
        setInt(4, it.toInt())
    } ?: setNull(4, Types.INTEGER)
    setInt(5, userID.toInt())
}


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
    id=getString("id"),
    date=getString("date").toLocalDate(),
    duration=Duration(getLong("duration")),
    sport=getInt("sport").toString(),
    route=getInt("route").toString(),
    user=getInt("user").toString()
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


