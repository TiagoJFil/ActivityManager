package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.repository.db.RouteID

import pt.isel.ls.repository.memory.UserID

typealias SportID = String

@Serializable
data class Sport(val id : SportID, val name: String, val description : String? = null, val user: UserID)


typealias ActivityID = String

@Serializable
data class Activity(val id : String, val date : String, val duration : Int, val sport : SportID, val route : RouteID? = null, val user : UserID)
