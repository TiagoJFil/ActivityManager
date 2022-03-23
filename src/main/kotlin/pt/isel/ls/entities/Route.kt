package pt.isel.ls.entities

import kotlinx.serialization.Serializable

typealias RouteID = String

@Serializable
data class Route(
    val id: String,
    val startLocation: String,
    val endLocation: String,
    val distance: Double,
    val user: UserID
)

