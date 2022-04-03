package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.entities.User
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import kotlin.contracts.ExperimentalContracts


inline fun <R> Connection.transaction(block: () -> R): R{
    autoCommit = false
    try {
        val rv = block()
        commit()
        return rv
    } catch (e: SQLException) {
        rollback()
        throw e // TODO: Throw DatabaseException
    } finally {
        autoCommit = true
    }
}

fun PreparedStatement.generatedKey(): String {
    generatedKeys.use {
        if(!it.next()) throw SQLException("No generated key")
        return it.getInt(1).toString()
    }
}

class UserDBRepository(val dataSource: PGSimpleDataSource) : UserRepository {

    /**
     * Returns the user with the given id.
     * @param userID the id of the user to be returned.
     * @return the user with the given id.
     */
    override fun getUserByID(userID: String): User? {
        TODO("Not yet implemented")
    }

    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     * @return [UserID] of the added user.
     */
    override fun addUser(userName: String, email: User.Email, userAuthToken: UserToken): UserID {


        fun PreparedStatement.update(param: String) {
            setString(1, param)
            executeUpdate()
        }

        dataSource.connection.use { connection ->

            return connection.transaction {
                val userID: UserID
                connection.prepareStatement("""INSERT INTO "user" (name) VALUES (?)""", Statement.RETURN_GENERATED_KEYS).use { preparedStatement ->
                    preparedStatement.update(userName)

                    userID = preparedStatement.generatedKey()
                }

                connection.prepareStatement("""INSERT INTO email ("user", email) VALUES ($userID,?)""").use { preparedStatement ->
                    preparedStatement.update(email.value)
                }

                connection.prepareStatement("""INSERT INTO tokens ("user", token) VALUES ($userID, ?)""").use { preparedStatement ->
                    preparedStatement.update(userAuthToken)
                }

                return@transaction userID
            }
        }
    }

    /**
     * Gets all the users in the repository.
     */
    override fun getUsers(): List<User> {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the specified user has a repeated email

     * @param email the user's email
     * @return [Boolean] true if another user already has the given email or false if it doesn't
     */
    override fun hasRepeatedEmail(email: User.Email): Boolean {
        dataSource.connection.use{ connection ->
            connection.transaction {
                connection.prepareStatement("SELECT * FROM email WHERE email = ?").use { preparedStatement ->
                    preparedStatement.setString(1, email.value)
                    preparedStatement.executeQuery().use { resultSet ->
                        return resultSet.next()
                    }
                }
            }
        }
    }

    /**
     * Gets the user id by the given token
     * @param token the token of the user.
     * @return [UserID] the user id of the user with the given token.
     */
    override fun getUserIDByToken(token: UserToken): UserID? {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the user with the given id exists.
     *
     * @param userID the id of the user to be checked.
     * @return [Boolean] true if the user exists or false if it doesn't.
     */
    override fun hasUser(userID: UserID): Boolean {
        TODO("Not yet implemented")
    }

}