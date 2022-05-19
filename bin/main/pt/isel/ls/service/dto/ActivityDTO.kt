package pt.isel.ls.service.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

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
data class ActivityDTO(
    val id: ActivityID,
    val date: String,
    val duration: String,
    val sport: SportID,
    val route: RouteID? = null,
    val user: UserID
) {

    constructor(activityEntity: Activity) : this(
        activityEntity.id,
        activityEntity.date.toString(),
        activityEntity.duration.toFormat(),
        activityEntity.sport,
        activityEntity.route,
        activityEntity.user
    )
}
