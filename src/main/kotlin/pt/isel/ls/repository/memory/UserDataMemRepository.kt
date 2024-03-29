package pt.isel.ls.repository.memory

import pt.isel.ls.config.GUEST_PASSWORD
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.dto.UserDTO
import pt.isel.ls.service.entities.User
import pt.isel.ls.service.entities.User.Email
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.applyPagination

class UserDataMemRepository(guest: User, private val activityRepo: ActivityDataMemRepository) : UserRepository {

    private var currentID = 0

    /**
     * Mapping between a [UserToken] and a [UserID]
     */
    private val tokenTable: MutableMap<UserToken, Int> = mutableMapOf(GUEST_TOKEN to guest.id)

    /**
     * Mapping between an email and a [UserID]
     */
    private val emailsMap: MutableMap<String, Int> = mutableMapOf(guest.email.value to guest.id)

    private val passwordMap = mutableMapOf(guest.id to GUEST_PASSWORD)

    /**
     * Mapping between a [UserID] and it's identified [User]
     */
    private val usersMap: MutableMap<Int, User> = mutableMapOf(guest.id to guest)

    val map: Map<Int, User>
        get() = usersMap.toMap()

    /**
     * Checks if the specified user has a repeated email
     *
     *  to check
     * @param email the user's email
     * @return [Boolean] true if another user already has the given email or false if it doesn't
     */
    override fun hasRepeatedEmail(email: Email): Boolean =
        emailsMap[email.value] != null

    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     * @return the user's id.
     */
    override fun addUser(userName: String, email: Email, userAuthToken: UserToken, hashedPassword: String): UserID {
        val userId = ++currentID
        val user = User(userName, email, userId)
        usersMap[userId] = user
        passwordMap[userId] = hashedPassword
        emailsMap[email.value] = userId
        tokenTable[userAuthToken] = userId
        return userId
    }

    /**
     * Gets all the users stored
     */
    override fun getUsers(paginationInfo: PaginationInfo): List<User> =
        usersMap.values
            .toList()
            .applyPagination(paginationInfo)

    /**
     * Gets the users that have an activity matching the given sport id and route id.
     * @param sportID sport identifier
     * @param routeID route identifier
     * @return [List] of [User] sorted by activity duration ASCENDING
     */
    override fun getUsersBy(sportID: SportID, routeID: RouteID, paginationInfo: PaginationInfo): List<User> =
        activityRepo.map.values
            .filter { it.route == routeID && it.sport == sportID }
            .sortedBy { it.duration.millis }
            .mapNotNull { usersMap[it.user] }
            .distinct()
            .applyPagination(paginationInfo)

    /**
     * Checks if the user with the given id exists
     */
    override fun hasUser(userID: UserID): Boolean = usersMap.containsKey(userID)

    /**
     * @param token user's unique token
     * @return the [UserID] identified by the [UserToken]
     */
    override fun getUserIDBy(token: UserToken): UserID? = tokenTable[token]

    /**
     * Gets the user token of the user with the given email.
     * @param email the email of the user.
     * @param passwordHash the password hash token of the user.
     * @return [UserToken] the user token of the user with the given email.
     */
    override fun getUserInfoByAuth(email: Email, passwordHash: String): Pair<UserToken, UserID>? {
        val userId = emailsMap[email.value] ?: return null
        val storedPassword = passwordMap[userId] ?: return null
        return if (storedPassword == passwordHash) {
            val token = tokenTable.entries.find { it.value == userId }?.key ?: return null
            token to userId
        } else {
            null
        }
    }

    /**
     * @param userID user's unique identifier
     * @return A [UserDTO] object or null if there is no user identified by [userID]
     */
    override fun getUserBy(userID: UserID): User? = usersMap[userID]
}
