package pt.isel.ls.services



import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

class UserServices(val repository: UserRepository) {

    /**
     * Verifies the parameters received and calls the function [UserRepository] to create a [User].
     * @param name the user's name
     * @param email the user's email
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(name : String?, email: String?) : Pair<UserToken,UserID>{
        if(name == null) throw MissingParameter("name")
        if(name.isBlank()) throw InvalidParameter("name")
        if(email == null) throw MissingParameter("email")
        if(email.isBlank()) throw InvalidParameter("email")

        val userId = generateRandomId()
        val userAuthToken = generateUUId()

        val possibleEmail = User.Email(email)
        val user =  User(name, possibleEmail,userId)

        if(repository.userHasRepeatedEmail(userId,possibleEmail)) throw InvalidParameter("email already exists")
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
        if (id == null) throw MissingParameter("id")
        if (id.isBlank()) throw InvalidParameter("id")
        return repository.getUserByID(id) ?: throw ResourceNotFound("User", "$id")
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


