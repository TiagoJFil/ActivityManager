package pt.isel.ls.service

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.dto.UserDTO
import pt.isel.ls.service.entities.User
import pt.isel.ls.service.entities.User.Email
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.service.generateUUId
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction

class UserServices(
    private val userRepository: UserRepository
) {

    companion object {
        val logger = getLoggerFor<UserServices>()
        const val NAME_PARAM = "name"
        const val EMAIL_PARAM = "email"
        const val EMAIL_TAKEN = "Email already taken"
        const val USER_ID_PARAM = "userID"
        const val RESOURCE_NAME = "User"
    }

    /**
     * Verifies the parameters received and calls the function [UserRepository] to create a [UserDTO].
     * @param name the user's name
     * @param email the user's email
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(name: String?, email: String?): Pair<UserToken, UserID> {
        logger.traceFunction(::createUser.name) {
            listOf(NAME_PARAM to name, EMAIL_PARAM to email)
        }

        val safeName = requireParameter(name, NAME_PARAM)
        val safeEmail = requireParameter(email, EMAIL_PARAM)

        val userAuthToken = generateUUId()

        val possibleEmail = Email(safeEmail)

        if (userRepository.hasRepeatedEmail(possibleEmail))
            throw InvalidParameter(EMAIL_TAKEN)

        val userID = userRepository.addUser(safeName, possibleEmail, userAuthToken)

        return Pair(userAuthToken, userID)
    }

    /**
     * Verifies the parameters received and calls the function [UserRepository] to get an [UserDTO].
     *
     * @param uid the unique id that identifies the user
     * @return [UserDTO] the user identified by the given id
     * @throws IllegalArgumentException
     */
    fun getUserByID(uid: Param): UserDTO {
        logger.traceFunction(::getUserByID.name) { listOf(USER_ID_PARAM to uid) }

        val safeUserID = requireParameter(uid, USER_ID_PARAM)
        val uidInt = requireIdInteger(safeUserID, USER_ID_PARAM)
        return userRepository.getUserBy(uidInt)?.toDTO()
            ?: throw ResourceNotFound(RESOURCE_NAME, "$uid")
    }

    /**
     * Calls the function [UserRepository] to get all the users.
     *
     * @return [List] of [User
     */
    fun getUsers(paginationInfo: PaginationInfo): List<UserDTO> {
        logger.traceFunction(::getUsers.name)

        return userRepository
            .getUsers(paginationInfo)
            .map(User::toDTO)
    }
}
