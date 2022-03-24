package pt.isel.ls.services



import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User
import pt.isel.ls.repository.memory.UserID
import pt.isel.ls.repository.memory.UserToken

class UserServices(val repository: UserRepository) {

    /**
     * Verifies the parameters received and creates calls the function [UserRepository] to create a [User].
     * @param name the user's name
     * @param email the user's email
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
     * Verifies the parameters received and calls the function [UserRepository] to get an [User].
     *
     * @param id the unique id that identifies the user
     * @return [User] the user identified by the given id
     * @throws IllegalArgumentException
     */
    fun getUserByID(id: UserID?): User {
        requireNotNull(id){" Parameter id is required. "}
        require(id.isNotBlank()) {" id field has no value "}
        val user: User? = repository.getUserByID(id)
        checkNotNull(user){" User does not exist."}

        return user
    }

    /**
     * Calls the function [UserRepository] to get all the users.
     *
     * @return [List] of [User]
     */
    fun getUsers(): List<User> = repository.getUsers()

    /**
     * Gets the user id that matches the given token
     *
     * @param token the user's unique token
     */
    fun getUserByToken(token: UserToken): UserID =
        repository.getUserIDByToken(token) ?: throw IllegalAccessException("Invalid Token")
}


