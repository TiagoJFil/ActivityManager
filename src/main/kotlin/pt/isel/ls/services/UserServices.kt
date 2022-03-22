package pt.isel.ls.services

import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import java.lang.Math.random

class UserServices(val userRepository: UserRepository) {

    fun getUserByID(id: String?): User {
        requireNotNull(id){" Parameter id is required. "}
        val user: User? = userRepository.getUserByID(id)
        checkNotNull(user){" User does not exist."}
        (System.currentTimeMillis() * random()).toInt().toString()
        return user
    }

}


