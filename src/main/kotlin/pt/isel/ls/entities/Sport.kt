package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.repository.UserID


typealias SportID = String

@Serializable
data class Sport(val id : SportID, val name: String, val description : String? = null, val user: UserID)

