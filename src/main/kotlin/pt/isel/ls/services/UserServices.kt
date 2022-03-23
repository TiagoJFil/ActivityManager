package pt.isel.ls.services



import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User
import pt.isel.ls.entities.UserID
import pt.isel.ls.entities.UserToken

class UserServices(val repository: UserRepository) {

    /**
     * Verifies the parameters received and creates calls the function [UserRepository] to create a [User]
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(name : String?, email: String?) : Pair<UserToken,UserID>{
        requireNotNull(name) {" Missing name."}
        requireNotNull(email) {" Missing email."}
        require(email.isNotBlank()) {" Email field has no value"}
        require(name.isNotBlank()) {" Name field has no value"}
        val userId = generateRandomId()
        val userAuthToken = generateUUId()

        val possibleEmail = User.Email(email)
        val user =  User(name, possibleEmail,userId)

        if(repository.userHasRepeatedEmail(userId,possibleEmail)) throw IllegalArgumentException(" Email already registered.")
        repository.addUser(user,userId,userAuthToken)

        return Pair(userAuthToken,userId)
    }

    /**
     *
     */
    fun getUserByID(id: UserID?): User {
        requireNotNull(id){" Parameter id is required. "}
        val user: User? = repository.getUserByID(id)
        checkNotNull(user){" User does not exist."}

        return user
    }

    fun getUsers(): List<User> = repository.getUsers()

    fun getUserByToken(token: UserToken): UserID =
        repository.getUserIDByToken(token) ?: throw IllegalAccessException("Invalid Token")
}


