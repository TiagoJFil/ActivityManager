package pt.isel.ls.entities

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import java.text.SimpleDateFormat
import java.util.*


@Serializable
data class Activity(
    val id: String,
    val date: LocalDate,
    val duration: String,
    val sport: SportID,
    val route: RouteID? = null,
    val user: UserID
)

fun main(){
    val pattern = "yyyy-MM-dd"
    val simpleDateFormat = SimpleDateFormat(pattern)
    val a = "2002 12 23".toLocalDateTime()
    val date = simpleDateFormat.format(Date())
    println(date)
}