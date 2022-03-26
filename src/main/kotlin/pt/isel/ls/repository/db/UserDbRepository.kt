package pt.isel.ls.repository.db

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken


private val jdbcDatabaseURL: String = System.getenv("JDBC_DATABASE_URL")

class UserDbRepository: UserRepository {
    private val dataSource = PGSimpleDataSource()
    private val userTable = "users"
    override fun getUserByID(id: UserID): User? {
    TODO()
    /*

        dataSource.setURL(jdbcDatabaseURL)
        dataSource.connection.use {
            it.createStatement().use { statement ->
                statement.apply {
                    val res = executeQuery("select * from $userTable where id = $id )")



                }
            }
        }
        dataSource.connection.use {
            val pstmt =it.prepareStatement("select * from $userTable where id = ?)")

            pstmt.setString(1,id)
            pstmt.
            pstmt.apply {
                val res = this.executeUpdate()

            }
        }
        */

    }

            override fun addUser(newUser: User, userAuthToken: UserToken) {
                dataSource.setURL(jdbcDatabaseURL)
                dataSource.connection.use {
                    val pstmt =it.prepareStatement("insert into $userTable values (?,?,?)")

                    pstmt.setString(1,newUser.id)   //fixx
                    pstmt.setString(2,newUser.email.value)
                    pstmt.setString(3,newUser.name)
                    pstmt.apply {
                        this.executeUpdate()

                    }
                }

            }

    override fun getUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override fun userHasRepeatedEmail(userId: UserID, email: User.Email): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserIDByToken(token: UserToken): UserID? {
        TODO("Not yet implemented")
    }

    override fun hasUser(userID: UserID): Boolean {
        TODO("Not yet implemented")
    }


}