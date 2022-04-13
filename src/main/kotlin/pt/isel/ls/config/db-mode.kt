package pt.isel.ls.config

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

private data class DbInfo(val url: String, val user: String, val password: String, val dataBase: String)

private fun getDbConnectionInfo(): DbInfo {
    val requireEnvVariable = { name: String ->
        System.getenv(name) ?: error("Please specify JDBC_DATABASE_URL environment variable")
    }

    return DbInfo(
        url = requireEnvVariable("JDBC_DATABASE_URL"),
        user = requireEnvVariable("JDBC_DATABASE_USER"),
        password = requireEnvVariable("JDBC_DATABASE_PASSWORD"),
        dataBase = requireEnvVariable("JDBC_DATABASE_NAME")
    )
}

private fun postgreSQL(suffix: String): DbSource {

    val dbInfo = getDbConnectionInfo()

    val dataSource = PGSimpleDataSource().apply {
        setURL(dbInfo.url)
        user = dbInfo.user
        password = dbInfo.password
        databaseName = dbInfo.dataBase
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
