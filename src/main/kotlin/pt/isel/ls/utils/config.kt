package pt.isel.ls.utils

import pt.isel.ls.services.*

data class Environment(
        val userServices: UserServices,
        val activityServices: ActivityServices,
        val routeServices: RouteServices,
        val sportsServices: SportsServices
)

enum class EnvironmentType(val dbMode: DBMODE) {
    PROD(DBMODE.POSTGRES),
    TEST(DBMODE.MEMORY)
}

fun EnvironmentType.getEnv() : Environment {
    val dbInfo = dbMode.source()
    val userRepo = dbInfo.userRepository
    val routeRepo = dbInfo.routeRepository
    val sportsRepo = dbInfo.sportRepository
    val activityRepo = dbInfo.activityRepository

    val userServices = UserServices(userRepo)
    val routeServices = RouteServices(routeRepo, userRepo)
    val sportsServices = SportsServices(sportsRepo,userRepo)
    val activityServices = ActivityServices(activityRepo, userRepo, sportsRepo,routeRepo)

    return Environment(userServices, activityServices, routeServices, sportsServices)
}








