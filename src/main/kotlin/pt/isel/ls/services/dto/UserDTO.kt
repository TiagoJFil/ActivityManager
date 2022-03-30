package pt.isel.ls.services.dto

import kotlinx.serialization.Serializable
import pt.isel.ls.services.entities.User
import pt.isel.ls.utils.UserID

/**
 * Represents a user
 *
 * @property name respective username
 * @property email respective user email
 * @property id unique identifier
 *
 */
@Serializable
data class UserDTO(
        val name: String,
        val email: String,
        val id: UserID
) {
    constructor(userEntity: User) : this(
            userEntity.name,
            userEntity.email.value,
            userEntity.id
    )
}

