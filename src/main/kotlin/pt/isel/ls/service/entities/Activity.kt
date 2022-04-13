package pt.isel.ls.service.entities

import kotlinx.datetime.LocalDate
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

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
data class Activity(
    val id: ActivityID,
    val date: LocalDate,
    val duration: Duration,
    val sport: SportID,
    val route: RouteID? = null,
    val user: UserID
) {
    /**
     * Represents an activity's duration
     *
     * @param millis duration in milliseconds
     */
    data class Duration(val millis: Long) {
        companion object {
            val format: DateFormat = SimpleDateFormat("HH:mm:ss.SSS")
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
        }

        /**
         * Formats the duration to a string with the pattern [HH:mm:ss.SSS]
         */
        fun toFormat(): String = format.format(Date(millis))
    }
}
