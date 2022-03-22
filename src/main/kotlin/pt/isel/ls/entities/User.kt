package pt.isel.ls.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.repository.memory.UserId


@Serializable
data class User(val name: String, val email: Email, val id: UserId)

@Serializable
data class Email(val value: String){
    companion object{
        private val emailRegex = Regex(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        )
    }

    init {
        require(emailRegex.matches(value)){" Email has the wrong format."}
    }
}

