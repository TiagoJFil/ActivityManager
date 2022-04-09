package pt.isel.ls.config

import pt.isel.ls.service.ActivityServices
import pt.isel.ls.service.RouteServices
import pt.isel.ls.service.SportsServices
import pt.isel.ls.service.UserServices

private const val DEFAULT_PORT = 9000

data class Environment(
    val userServices: UserServices,
    val activityServices: ActivityServices,
    val routeServices: RouteServices,
    val sportsServices: SportsServices,
    val serverPort: Int
)

enum class EnvironmentType(val dbMode: DBMODE) {
    PROD(DBMODE.POSTGRESQL),
    TEST(DBMODE.MEMORY),
}

fun EnvironmentType.getEnv(): Environment {

    val dbInfo = dbMode.source()
    val userRepo = dbInfo.userRepository
    val routeRepo = dbInfo.routeRepository
    val sportsRepo = dbInfo.sportRepository
    val activityRepo = dbInfo.activityRepository

    val port = System.getenv("SERVER_PORT")?.toInt() ?: DEFAULT_PORT

    val userServices = UserServices(userRepo)
    val routeServices = RouteServices(routeRepo, userRepo)
    val sportsServices = SportsServices(sportsRepo, userRepo)
    val activityServices = ActivityServices(activityRepo, userRepo, sportsRepo, routeRepo)

    return Environment(userServices, activityServices, routeServices, sportsServices, port)
}
