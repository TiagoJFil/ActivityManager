package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.repository.RouteID
import pt.isel.ls.repository.UserID


@Serializable
data class Activity(val id : String, val date : String, val duration : Int, val sport : SportID, val route : RouteID? = null, val user : UserID)
