package pt.isel.ls.utils.repository.transactions

import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testActivity
import pt.isel.ls.config.testRoute
import pt.isel.ls.config.testSport
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.repository.database.ActivityDBRepository
import pt.isel.ls.repository.database.RouteDBRepository
import pt.isel.ls.repository.database.SportDBRepository
import pt.isel.ls.repository.database.UserDBRepository
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository

/**
 * A [TransactionScope] is a scope for a transaction.
 *
 * It is used to provide the necessary repositories for the transaction.
 */
sealed class TransactionScope(val transaction: Transaction) {
    abstract val sportsRepository: SportRepository
    abstract val routesRepository: RouteRepository
    abstract val activitiesRepository: ActivityRepository
    abstract val usersRepository: UserRepository
}

/**
 * A [TransactionScope] for a transaction in JDBC.
 *
 * It is responsible for making sure the connection used in the transaction is the same used to make queries
 * to the database inside the repositories.
 *
 */
class JDBCTransactionScope(transaction: JDBCTransaction) : TransactionScope(transaction) {

    override val sportsRepository: SportRepository
        by lazy { SportDBRepository(transaction.connection) }

    override val routesRepository: RouteRepository
        by lazy { RouteDBRepository(transaction.connection) }

    override val activitiesRepository: ActivityRepository
        by lazy { ActivityDBRepository(transaction.connection) }

    override val usersRepository: UserRepository
        by lazy { UserDBRepository(transaction.connection) }
}

/**
 * A [TransactionScope] for a transaction in memory.
 *
 * Provides repositories for the transaction and a method to reset the repositories.
 */
object InMemoryTransactionScope : TransactionScope(InMemoryTransaction) {

    override var sportsRepository: SportRepository = SportDataMemRepository(testSport)
        private set

    override var routesRepository: RouteRepository = RouteDataMemRepository(testRoute)
        private set

    override var usersRepository: UserRepository = UserDataMemRepository(guestUser)
        private set

    override var activitiesRepository: ActivityRepository = ActivityDataMemRepository(testActivity, usersRepository as UserDataMemRepository)
        private set

    /**
     * Resets the repositories to their initial state.
     */
    fun reset() {
        sportsRepository = SportDataMemRepository(testSport)
        routesRepository = RouteDataMemRepository(testRoute)
        usersRepository = UserDataMemRepository(guestUser)
        activitiesRepository = ActivityDataMemRepository(
            testActivity,
            usersRepository as UserDataMemRepository
        )
    }
}
