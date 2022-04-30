package pt.isel.ls.config

import org.postgresql.ds.PGSimpleDataSource
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
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

private val logger = LoggerFactory.getLogger("pt.isel.ls.config.db-mode")

enum class DBMODE { MEMORY, POSTGRESQL }

fun DBMODE.source(): DbSource? =
    when (this) {
        DBMODE.MEMORY -> memory()
        DBMODE.POSTGRESQL -> postgreSQL("_prod")
    }

class DbSource(
    val userRepository: UserRepository,
    val routeRepository: RouteRepository,
    val sportRepository: SportRepository,
    val activityRepository: ActivityRepository
)

private fun postgreSQL(suffix: String): DbSource? {

    val dbInfo = dbConnectionInfo()

    val dataSource = PGSimpleDataSource().apply {
        setURL(dbInfo.url)
        user = dbInfo.user
        password = dbInfo.password
        databaseName = dbInfo.dataBase
    }
    try {
        dataSource.connection
    } catch (e: PSQLException) {
        logger.error("Could not start database with the information given. Please check your JDBC environment variables.")
        return null
    }

    return DbSource(
        userRepository = UserDBRepository(dataSource, suffix),
        routeRepository = RouteDBRepository(dataSource, suffix),
        sportRepository = SportDBRepository(dataSource, suffix),
        activityRepository = ActivityDBRepository(dataSource, suffix)
    )
}

private fun memory(): DbSource {
    val userRepository = UserDataMemRepository(guestUser)
    return DbSource(
        userRepository,
        RouteDataMemRepository(testRoute),
        SportDataMemRepository(testSport),
        ActivityDataMemRepository(testActivity, userRepository)
    )
}
