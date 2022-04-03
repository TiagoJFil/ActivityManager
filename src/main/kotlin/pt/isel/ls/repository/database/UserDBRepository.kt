package pt.isel.ls.repository.database

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.repository.database.utils.transaction
import pt.isel.ls.services.entities.User
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

class UserDBRepository(private val dataSource: PGSimpleDataSource) : UserRepository {

    /**
     * Returns the user with the given id.
     * @param userID the id of the user to be returned.
     * @return the user with the given id.
     */
    override fun getUserByID(userID: String): User? {
        dataSource.connection.use { connection->
             return connection.transaction{
                 val email : String
                 val user: User?
                 connection.createStatement().use { statement ->
                     statement.executeQuery("""SELECT email FROM email WHERE "user" = $userID""")
                     email = statement.resultSet.use { emailResultSet ->
                         if(!emailResultSet.next()) return null
                         emailResultSet.getString("email")
                     }
                 }

                 connection.createStatement().use {
                     it.executeQuery("""SELECT * FROM "user" WHERE id = $userID""")

                     user = it.resultSet.use { userResultSet->
                         if(!userResultSet.next()) return null
                         val uid: UserID = userResultSet.getInt("id").toString()
                         val name = userResultSet.getString("name")

                         User(name, User.Email(email), uid)

                     }
                 }
                 user
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