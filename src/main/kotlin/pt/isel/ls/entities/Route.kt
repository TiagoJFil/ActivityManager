package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.repository.db.RouteID
import pt.isel.ls.repository.memory.UserID


@Serializable
data class Route(
    val id: RouteID,
    val startLocation: String,
    val endLocation: String,
    val distance: Double,
    val user: UserID
)

