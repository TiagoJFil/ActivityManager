package pt.isel.ls.services



import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

class UserServices(
    private val userRepository: UserRepository
    ) {

    /**
     * Verifies the parameters received and calls the function [UserRepository] to create a [User].
     * @param name the user's name
     * @param email the user's email
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(name : String?, email: String?) : Pair<UserToken,UserID>{
        val safeName = requireParameter(name, "name")
        val safeEmail = requireParameter(email, "email")

        val userId = generateRandomId()
        val userAuthToken = generateUUId()

        val possibleEmail = User.Email(safeEmail)
        val user =  User(safeName, possibleEmail,userId)

        if(userRepository.userHasRepeatedEmail(userId,possibleEmail)) throw InvalidParameter("email already exists")
        userRepository.addUser(user,userAuthToken)


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
        val safeUserID = requireParameter(id, "id")
        return userRepository.getUserByID(safeUserID) ?: throw ResourceNotFound("User", "$id")
    }

    /**
     * Calls the function [UserRepository] to get all the users.
     *
     * @return [List] of [User]
     */
    fun getUsers(): List<User> = userRepository.getUsers()

    /**
     * Gets the user id that matches the given token
     *
     * @param token the user's unique token
     */
    fun getUserByToken(token: UserToken?): UserID{
        if(token == null) throw UnauthenticatedError()
        return userRepository.getUserIDByToken(token) ?: throw UnauthenticatedError()
    }

}


