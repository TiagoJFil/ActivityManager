package pt.isel.ls.service.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

/**
 * Represents a sport
 *
 * @property id respective sport id
 * @property name respective sport's name
 * @property description respective sport's description
 * @property user respective sport's user
 */
@Serializable
data class SportDTO(
    val id: SportID,
    val name: String,
    val description: String? = null,
    val user: UserID
) {
    constructor(sportEntity: Sport) : this(
        sportEntity.id,
        sportEntity.name,
        sportEntity.description,
        sportEntity.user
    )
}
