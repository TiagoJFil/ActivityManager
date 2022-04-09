package pt.isel.ls.utils

import org.postgresql.ds.PGSimpleDataSource
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

enum class DBMODE {
    MEMORY,
    POSTGRESQL,
}

class DbSource(
    val userRepository: UserRepository,
    val routeRepository: RouteRepository,
    val sportRepository: SportRepository,
    val activityRepository: ActivityRepository
)

fun DBMODE.source(): DbSource =
    when (this) {
        DBMODE.MEMORY -> memory()
        DBMODE.POSTGRESQL -> postgreSQL("_prod")
    }

private fun postgreSQL(suffix: String): DbSource {

    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        ?: error("Please specify JDBC_DATABASE_URL environment variable")

    val dataSource = PGSimpleDataSource().apply {
        setURL(jdbcDatabaseURL)
    }

    return DbSource(
        userRepository = UserDBRepository(dataSource, suffix),
        routeRepository = RouteDBRepository(dataSource, suffix),
        sportRepository = SportDBRepository(dataSource, suffix),
        activityRepository = ActivityDBRepository(dataSource, suffix)
    )
}

private fun memory() = DbSource(
    UserDataMemRepository(guestUser),
    RouteDataMemRepository(testRoute),
    SportDataMemRepository(testSport),
    ActivityDataMemRepository(testActivity)
)
