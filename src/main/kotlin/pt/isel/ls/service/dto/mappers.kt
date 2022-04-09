package pt.isel.ls.service.dto

import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Route
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.service.entities.User

/**
 * Entity converters to its DTO
 */

fun User.toDTO() = UserDTO(this)
fun Route.toDTO() = RouteDTO(this)
fun Activity.toDTO() = ActivityDTO(this)
fun Sport.toDTO() = SportDTO(this)
