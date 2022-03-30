package pt.isel.ls.services.dto

import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Route
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.services.entities.User


/**
 * Entity converters to its DTO
 */


fun User.toDTO() = UserDTO(this)
fun Route.toDTO() = RouteDTO(this)
fun Activity.toDTO() = ActivityDTO(this)
fun Sport.toDTO() = SportDTO(this)