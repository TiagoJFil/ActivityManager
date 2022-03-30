package pt.isel.ls.services


import pt.isel.ls.services.dto.UserDTO
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.entities.User.Email

class UserServices(
        private val userRepository: UserRepository
) {

    /**
     * Verifies the parameters received and calls the function [UserRepository] to create a [UserDTO].
     * @param name the user's name
     * @param email the user's email
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(name: String?, email: String?): Pair<UserToken, UserID> {
        val safeName = requireParameter(name, "name")
        val safeEmail = requireParameter(email, "email")

        val userId = generateRandomId()
        val userAuthToken = generateUUId()

        val possibleEmail = Email(safeEmail)

        if (userRepository.hasRepeatedEmail(possibleEmail))
            throw InvalidParameter("email already exists")

        userRepository.addUser(safeName, possibleEmail, userId, userAuthToken)


        return Pair(userAuthToken, userId)
    }

    /**
     * Verifies the parameters received and calls the function [UserRepository] to get an [UserDTO].
     *
     * @param uid the unique id that identifies the user
     * @return [UserDTO] the user identified by the given id
     * @throws IllegalArgumentException
     */
    fun getUserByID(uid: UserID?): UserDTO {
        val safeUserID = requireParameter(uid, "id")
        return userRepository.getUserByID(safeUserID)?.toDTO()
                ?: throw ResourceNotFound("User", "$uid")
    }

    /**
     * Calls the function [UserRepository] to get all the users.
     *
     * @return [List] of [User
     */
     fun getUsers(): List<UserDTO> = userRepository
                .getUsers()
                .map(User::toDTO)



}


