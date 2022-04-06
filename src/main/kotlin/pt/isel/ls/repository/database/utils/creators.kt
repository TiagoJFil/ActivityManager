package pt.isel.ls.repository.database.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Types


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

fun PreparedStatement.setActivity(
    date: LocalDate,
    duration: Activity.Duration,
    sportID: SportID,
    routeID: RouteID?,
    userID: UserID
) {
    setDate(1, Date.valueOf(date.toJavaLocalDate()))
    setLong(2, duration.millis)
    setInt(3, sportID.toInt())
    routeID?.let {
        setInt(4, it.toInt())
    } ?: setNull(4, Types.INTEGER)
    setInt(5, userID.toInt())
}