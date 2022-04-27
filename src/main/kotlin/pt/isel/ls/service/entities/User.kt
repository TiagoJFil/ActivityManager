package pt.isel.ls.service.entities

import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.utils.UserID

/**
 * Represents a user
 *
 * @property name respective username
 * @property email respective user email
 * @property id unique identifier
 *
 */
data class User(val name: String, val email: Email, val id: UserID) {

    /**
     * Represents an email from a [User]
     * @throws IllegalArgumentException if the email is not valid (wrong email format)
     * @property value String value of the email
     * */
    data class Email(val value: String) {
        companion object {
            const val MAX_NAME_LENGTH = 20
            private val emailRegex = Regex(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)" +
                    "*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
            )
        }

        init {
            if (!emailRegex.matches(value)) throw InvalidParameter("email")
        }
    }
}
