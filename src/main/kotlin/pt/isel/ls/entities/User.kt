package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.repository.memory.UserID

/**
 * Represents a user
 *
 * @property name respective username
 * @property email respective user email
 * @property id unique identifier
 *
 */
@Serializable
data class User(val name: String, val email: Email, val id: UserID){

    /**
     * Represents an email from a [User]
     * @throws IllegalArgumentException if the email is not valid (wrong email format)
     * @property value String value of the email
     */
    @Serializable
    data class Email(val value: String){
        companion object{
            private val emailRegex = Regex(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
            )

            private const val INVALID_FORMAT = "Email has the wrong format."
        }

        init {
            require(emailRegex.matches(value)){ INVALID_FORMAT }
        }

    }

}

