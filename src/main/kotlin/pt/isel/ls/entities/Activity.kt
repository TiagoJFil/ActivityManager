package pt.isel.ls.entities

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import java.text.SimpleDateFormat
import java.util.*


/**
 * Represent an activity
 *
 * @param id the respective activity's id
 * @param date activity's date
 * @param duration activity's duration
 * @param sport activity's sport id
 * @param route activity's route id, null by default
 * @param user activity's user
 */

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