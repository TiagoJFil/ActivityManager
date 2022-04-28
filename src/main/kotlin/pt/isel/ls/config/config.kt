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

private fun getEnvType(): EnvironmentType {

    try {
        val envType = System.getenv("APP_ENV_TYPE")
        return EnvironmentType.valueOf(envType)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Please specify a valid APP_ENV_TYPE: ${EnvironmentType.values().toList()}")
    }
}
fun getEnv(testMode: Boolean = false): Environment? {
    val envType = if (testMode) EnvironmentType.TEST else getEnvType()

    val dbInfo = envType.dbMode.source() ?: return null
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
