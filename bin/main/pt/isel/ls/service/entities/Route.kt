package pt.isel.ls.service.entities

import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.UserID

/**
 * Represents a route
 *
 * @property id respective route's id
 * @property startLocation route's start location
 * @property endLocation route's end location
 * @property distance route's distance
 * @property user route's user
 */
data class Route(
    val id: RouteID,
    val startLocation: String,
    val endLocation: String,
    val distance: Double,
    val user: UserID
) {
    companion object {
        const val MAX_LOCATION_LENGTH = 150
    }
}
