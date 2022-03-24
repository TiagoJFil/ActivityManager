package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

@Serializable
data class Activity(
    val id: String,
    val date: String,
    val duration: Int,
    val sport: SportID,
    val route: RouteID? = null,
    val user: UserID
)
