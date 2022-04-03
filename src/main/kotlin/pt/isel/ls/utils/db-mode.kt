package pt.isel.ls.utils

import kotlinx.datetime.toLocalDate
import org.postgresql.ds.PGSimpleDataSource
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
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.generateUUId


enum class DBMODE {
    MEMORY,
    POSTGRES
}

class DbSource(
    val userRepository: UserRepository,
    val routeRepository: RouteRepository,
    val sportRepository: SportRepository,
    val activityRepository: ActivityRepository
)

fun DBMODE.source(): DbSource =
    when(this){
        DBMODE.MEMORY -> memory()
        DBMODE.POSTGRES -> postgres()
    }


private fun postgres(): DbSource{

    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        ?: error("Please specify JDBC_DATABASE_URL environment variable")
    val dataSource = PGSimpleDataSource().apply {
        setURL(jdbcDatabaseURL)
    }

    return DbSource(
        userRepository = UserDBRepository(dataSource),
        routeRepository = RouteDBRepository(dataSource),
        sportRepository = SportDBRepository(dataSource),
        activityRepository = ActivityDBRepository(dataSource)
    )
}


private fun memory() = DbSource(
    UserDataMemRepository(guestUser),
    RouteDataMemRepository(testRoute),
    SportDataMemRepository(testSport),
    ActivityDataMemRepository(testActivity)
)


