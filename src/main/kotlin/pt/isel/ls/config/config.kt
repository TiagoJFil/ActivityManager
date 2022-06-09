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
    TEST(DBMODE.MEMORY)
}

private fun getEnvType(): EnvironmentType {

    try {
        val envType = System.getenv("APP_ENV_TYPE")
            ?: error("Please specify APP_ENV_TYPE environment variable")


        return when (envType) {
            "PROD" -> EnvironmentType.PROD
            "TEST" -> EnvironmentType.TEST
            else -> error("Invalid APP_ENV_TYPE: $envType")
        }
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Please specify a valid APP_ENV_TYPE: ${EnvironmentType.values().toList()}")
    }
}
fun getEnv(): Environment {
    val envType = getEnvType()
    val transactionFactory = envType.dbMode.transactionFactory
    val port = System.getenv("PORT")?.toInt() ?: DEFAULT_PORT

    return Environment(
        UserServices(transactionFactory),
        ActivityServices(transactionFactory),
        RouteServices(transactionFactory),
        SportsServices(transactionFactory),
        port
    )
}
