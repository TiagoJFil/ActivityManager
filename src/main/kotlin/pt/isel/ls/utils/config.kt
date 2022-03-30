package pt.isel.ls.utils

import kotlinx.datetime.toLocalDate
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.*
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Route
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.entities.User.Email


val guestUser = User(
    name = "guest",
    id = "guestID",
    email = Email("guest@gmail.com")
)

val testRoute = Route(
        id = generateRandomId(),
        startLocation = "testStartLocation",
        endLocation = "testEndLocation",
        distance = 23.0,
        user = guestUser.id
)

val testSport = Sport(
        id = generateRandomId(),
        name = "testSport",
        description = "testDescription",
        user = guestUser.id
)

val testActivity = Activity(
        id = generateRandomId(),
        sport = testSport.id,
        route = testRoute.id,
        user = guestUser.id,
        duration = Activity.Duration(500000),
        date = "2002-10-02".toLocalDate()
)
const val GUEST_TOKEN = "TOKEN"

data class Environment(
        val userServices: UserServices,
        val activityServices: ActivityServices,
        val routeServices: RouteServices,
        val sportsServices: SportsServices
)


enum class EnvironmentType(val environment: Environment){
    PROD(prodEnv()),
    TEST(testEnv())
}
fun EnvironmentType.getEnv() : Environment {
    return when(this){
        EnvironmentType.PROD ->  prodEnv()
        EnvironmentType.TEST -> testEnv()
    }
}

private fun prodEnv(): Environment {

    /**

    val userServices = UserServices(userRepo)

    val routeServices = RouteServices(routeRepo, userRepo)

    val sportsServices = SportsServices(sportsRepo,userRepo)

    val activityServices = ActivityServices(activityRepo, userRepo, sportsRepo,routeRepo)
**/
    return testEnv() //TODO: CHANGE
}

private fun testEnv(): Environment{
    val userRepo = UserDataMemRepository(guestUser)
    val routeRepo = RouteDataMemRepository(testRoute)
    val sportsRepo = SportDataMemRepository(testSport)
    val activityRepo = ActivityDataMemRepository(testActivity)


    val userServices = UserServices(userRepo)

    val routeServices = RouteServices(routeRepo, userRepo)

    val sportsServices = SportsServices(sportsRepo,userRepo)

    val activityServices = ActivityServices(activityRepo, userRepo, sportsRepo,routeRepo)
    return Environment(userServices, activityServices, routeServices, sportsServices)
}





