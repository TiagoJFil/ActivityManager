package pt.isel.ls.repository.database

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.entities.User
import pt.isel.ls.service.entities.User.Email
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.repository.activityTable
import pt.isel.ls.utils.repository.applyPagination
import pt.isel.ls.utils.repository.emailTable
import pt.isel.ls.utils.repository.generatedKey
import pt.isel.ls.utils.repository.ifNext
import pt.isel.ls.utils.repository.toListOf
import pt.isel.ls.utils.repository.toUser
import pt.isel.ls.utils.repository.tokenTable
import pt.isel.ls.utils.repository.userTable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class UserDBRepository(private val connection: Connection) : UserRepository {

    /**
     * Returns the user with the given id.
     * @param userID the id of the user to be returned.
     * @return the user with the given id.
     */
    override fun getUserBy(userID: UserID): User? {
        val email: String =
            connection.prepareStatement("""SELECT email FROM $emailTable WHERE "user" = ?""").use { statement ->
                statement.setInt(1, userID)
                statement.executeQuery().use { emailResultSet ->
                    emailResultSet.ifNext { emailResultSet.getString("email") } ?: return null
                }
            }

        return connection.prepareStatement("""SELECT id,name FROM $userTable WHERE id = ?""").use { statement ->
            statement.setInt(1, userID)
            statement.executeQuery().use { userResultSet ->
                userResultSet.ifNext {
                    userResultSet.toUser(Email(email))
                }
            }
        }
    }

    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     * @return [UserID] of the added user.
     */
    override fun addUser(userName: String, email: Email, userAuthToken: UserToken, hashedPassword: String): UserID {

        fun PreparedStatement.update(param: String) {
            setString(1, param)
            executeUpdate()
        }

        val addUserQuery = """INSERT INTO $userTable (name,password) VALUES (?,?)"""
        val userID: UserID = connection.prepareStatement(addUserQuery, Statement.RETURN_GENERATED_KEYS).use { preparedStatement ->
            preparedStatement.setString(1, userName)
            preparedStatement.setString(2, hashedPassword)
            preparedStatement.executeUpdate()

            preparedStatement.generatedKey()
        }

        val addEmailQuery = """INSERT INTO $emailTable ("user", email) VALUES ($userID,?)"""
        connection.prepareStatement(addEmailQuery).use { preparedStatement ->
            preparedStatement.update(email.value)
        }

        val addTokenQuery = """INSERT INTO $tokenTable ("user", token) VALUES ($userID,?)"""
        connection.prepareStatement(addTokenQuery).use { preparedStatement ->
            preparedStatement.update(userAuthToken)
        }

        return userID
    }

    /**
     * Gets all the users in the repository.
     */
    override fun getUsers(paginationInfo: PaginationInfo): List<User> {
        val emails = getEmails(paginationInfo)
        val query = """SELECT id, name FROM $userTable LIMIT ? OFFSET ?"""
        return connection.prepareStatement(query).use { statement ->
            statement.applyPagination(paginationInfo, indexes = Pair(1, 2))

            statement.executeQuery().use { resultSet ->
                emails.map { email ->
                    resultSet.ifNext {
                        val name = resultSet.getString("name")
                        val userID = resultSet.getInt("id")

                        User(name, email, userID)
                    } ?: throw IllegalStateException("Database has inconsistent data on emails and users")
                }
            }
        }
    }

    /**
     * Gets the users that have an activity matching the given sport id and route id.
     * @param sportID sport identifier
     * @param routeID route identifier
     * @return [List] of [User]
     */
    override fun getUsersBy(sportID: SportID, routeID: RouteID, paginationInfo: PaginationInfo): List<User> {
        val query =
            "select distinct * from (" +
                "SELECT $userTable.id ,$userTable.name, $emailTable.email " +
                "FROM $activityTable " +
                "JOIN $userTable ON ($activityTable.user = $userTable.id) " +
                "JOIN $emailTable ON ($emailTable.user = $userTable.id) " +
                "WHERE $activityTable.sport = ? AND $activityTable.route = ? " +
                "ORDER BY $activityTable.duration DESC " +
                "LIMIT ? OFFSET ?) as t"
        connection.prepareStatement(query).use {
            it.applyPagination(paginationInfo, indexes = Pair(3, 4))
            it.setInt(1, sportID)
            it.setInt(2, routeID)
            val rs = it.executeQuery()
            return rs.toListOf(ResultSet::toUser)
        }
    }

    /**
     * Gets all the emails of the users in the repository.
     * Must be called within a transaction block.
     *
     * @return a list of emails.
     */
    private fun getEmails(paginationInfo: PaginationInfo): List<Email> {
        val query = """SELECT email FROM $emailTable ORDER BY user LIMIT ? OFFSET ?"""
        connection.prepareStatement(query).use { statement ->
            statement.applyPagination(paginationInfo, indexes = Pair(1, 2))
            val resultSet = statement.executeQuery()
            return resultSet.toListOf<Email> {
                Email(resultSet.getString("email"))
            }
        }
    }

    /**
     * Checks if any existing user has the given email.
     * @param email the user's email
     * @return [Boolean] true if another user already has the given email or false if it doesn't
     */
    override fun hasRepeatedEmail(email: Email): Boolean {
        connection.prepareStatement("SELECT * FROM $emailTable WHERE email = ?").use { ps ->
            ps.setString(1, email.value)
            ps.executeQuery().use { resultSet ->
                return resultSet.next()
            }
        }
    }

    /**
     * Gets the user id by the given token
     * @param token the token of the user.
     * @return [UserID] the user id of the user with the given token.
     */
    override fun getUserIDBy(token: UserToken): UserID? {
        val query = "SELECT * FROM $tokenTable WHERE token = ?"
        connection.prepareStatement(query).use { stmt ->
            stmt.setString(1, token)
            stmt.executeQuery().use { resultSet ->
                return resultSet.ifNext {
                    resultSet.getInt("user")
                }
            }
        }
    }

    /**
     * Gets the user token of the user with the given email.
     * @param email the email of the user.
     * @param passwordHash the password token of the user.
     * @return [UserToken] the user token of the user with the given email.
     */
    override fun getUserInfoByAuth(email: Email, passwordHash: String): Pair<UserToken, UserID>? {

        val query = """
            SELECT token, token."user" as tuser FROM $userTable
            INNER JOIN $emailTable ON $userTable.id = $emailTable.user
            INNER JOIN $tokenTable ON $userTable.id = $tokenTable.user
            WHERE $emailTable.email = ? AND $userTable.password = ?
        """

        connection.prepareStatement(query).use { stmt ->
            stmt.setString(1, email.value)
            stmt.setString(2, passwordHash)
            stmt.executeQuery().use { resultSet ->
                return resultSet.ifNext {
                    val token = resultSet.getString("token")
                    val uid = resultSet.getInt("tuser")
                    token to uid
                }
            }
        }
    }

    /**
     * Checks if the user with the given id exists.
     *
     * @param userID the id of the user to be checked.
     * @return [Boolean] true if the user exists or false if it doesn't.
     */
    override fun hasUser(userID: UserID): Boolean {
        val statement = connection.prepareStatement("""SELECT * FROM $userTable WHERE id = ?""")
        statement.use { stmt ->
            stmt.setInt(1, userID)
            stmt.executeQuery().use { resultSet ->
                return resultSet.next()
            }
        }
    }
}
