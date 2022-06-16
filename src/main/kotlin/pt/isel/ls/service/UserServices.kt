package pt.isel.ls.service

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.dto.UserDTO
import pt.isel.ls.service.entities.User
import pt.isel.ls.service.entities.User.Email
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.loggerFor
import pt.isel.ls.utils.repository.transactions.TransactionFactory
import pt.isel.ls.utils.service.generateUUId
import pt.isel.ls.utils.service.hashPassword
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.requireRoute
import pt.isel.ls.utils.service.requireSport
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction

class UserServices(
    private val transactionFactory: TransactionFactory,
) {

    companion object {
        private val logger = loggerFor<UserServices>()

        const val NAME_PARAM = "User name"
        const val EMAIL_PARAM = "email"
        const val PASSWORD_PARAM: String = "password"
        const val USER_ID_PARAM = "userID"

        const val EMAIL_TAKEN = "Email already taken"
        const val INVALID_CREDENTIALS = "Invalid credentials"
        const val RESOURCE_NAME = "User"
    }

    /**
     * Verifies the parameters received and calls the function [UserRepository] to create a [UserDTO].
     * @param name the user's name
     * @param email the user's email
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(name: Param, email: Param, password: Param): Pair<UserToken, UserID> {
        logger.traceFunction(::createUser.name) {
            listOf(NAME_PARAM to name, EMAIL_PARAM to email)
        }
        val safeName = requireParameter(name, NAME_PARAM)
        val safeEmail = requireParameter(email, EMAIL_PARAM)
        val safePassword = requireParameter(password, PASSWORD_PARAM)

        val userAuthToken = generateUUId()
        val possibleEmail = Email(safeEmail)

        val hashedPassword = hashPassword(safePassword)

        return transactionFactory.getTransaction().execute {

            if (usersRepository.hasRepeatedEmail(possibleEmail))
                throw InvalidParameter(EMAIL_TAKEN)

            val userID = usersRepository.addUser(safeName, possibleEmail, userAuthToken, hashedPassword)

            return@execute Pair(userAuthToken, userID)
        }
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

        return transactionFactory.getTransaction().execute {
            usersRepository.getUserBy(uidInt)?.toDTO()
                ?: throw ResourceNotFound(RESOURCE_NAME, "$uid")
        }
    }

    /**
     * Calls the function [UserRepository] to get all the users.
     *
     * @return [List] of [User
     */
    fun getUsers(paginationInfo: PaginationInfo): List<UserDTO> {
        logger.traceFunction(::getUsers.name)

        return transactionFactory.getTransaction().execute {
            usersRepository
                .getUsers(paginationInfo)
                .map(User::toDTO)
        }
    }

    /**
     * Gets the users that match activities with the given sport id and route id.
     * @param sportID The sport id of the activity.
     * @param routeID The route id of the activity.
     */
    fun getUsersByActivity(sportID: Param, routeID: Param, paginationInfo: PaginationInfo): List<UserDTO> {
        logger.traceFunction(::getUsersByActivity.name) {
            listOf(
                SportsServices.SPORT_ID_PARAM to sportID,
                ActivityServices.ROUTE_ID_PARAM to routeID
            )
        }
        val safeSID = requireParameter(sportID, "SportID")
        val safeRID = requireParameter(routeID, "RouteID")
        val sidInt = requireIdInteger(safeSID, SportsServices.SPORT_ID_PARAM)

        return transactionFactory.getTransaction().execute {
            sportsRepository.requireSport(sidInt)
            val ridInt = requireIdInteger(safeRID, ActivityServices.ROUTE_ID_PARAM)
            routesRepository.requireRoute(ridInt)

            return@execute usersRepository
                .getUsersBy(sidInt, ridInt, paginationInfo)
                .map(User::toDTO)
        }
    }

    /**
     * Gets the token of the user that has the given email.
     * @param email the email of the user
     * @param password the password of the user
     * @return the token of the user
     */
    fun getUserInfoByAuth(email: Param, password: Param): Pair<UserToken, UserID> {
        logger.traceFunction(::getUserInfoByAuth.name) { listOf(EMAIL_PARAM to email) }

        val safeEmail = Email(requireParameter(email, EMAIL_PARAM))
        val safePassword = requireParameter(password, PASSWORD_PARAM)

        val passwordHash = hashPassword(safePassword)

        return transactionFactory.getTransaction().execute {

            usersRepository.getUserInfoByAuth(safeEmail, passwordHash)
                ?: throw InvalidParameter(INVALID_CREDENTIALS)
        }
    }
}
