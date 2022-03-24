package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.repository.RouteID
import pt.isel.ls.repository.UserID

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
data class Activity(val id : String, val date : String, val duration : Int, val sport : SportID, val route : RouteID? = null, val user : UserID)
