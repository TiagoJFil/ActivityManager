package pt.isel.ls.config

import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices


data class ServicesInfo(
    val userServices: UserServices,
    val activityServices: ActivityServices,
    val routeServices: RouteServices,
    val sportsServices: SportsServices
)

enum class EnvironmentType(val dbMode: DBMODE) {
    PROD(DBMODE.POSTGRES_PROD),
    TEST(DBMODE.MEMORY),
}

fun EnvironmentType.getEnv() : ServicesInfo {

    val dbInfo = dbMode.source()
    val userRepo = dbInfo.userRepository
    val routeRepo = dbInfo.routeRepository
    val sportsRepo = dbInfo.sportRepository
    val activityRepo = dbInfo.activityRepository

    val userServices = UserServices(userRepo)
    val routeServices = RouteServices(routeRepo, userRepo)
    val sportsServices = SportsServices(sportsRepo,userRepo)
    val activityServices = ActivityServices(activityRepo, userRepo, sportsRepo,routeRepo)

    return ServicesInfo(userServices, activityServices, routeServices, sportsServices)
}








