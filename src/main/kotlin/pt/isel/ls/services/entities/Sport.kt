package pt.isel.ls.services.entities

import kotlinx.serialization.Serializable
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
data class Sport(
        val id: SportID,
        val name: String,
        val description: String? = null,
        val user: UserID
)

