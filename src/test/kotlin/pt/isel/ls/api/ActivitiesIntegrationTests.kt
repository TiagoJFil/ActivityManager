package pt.isel.ls.api

import org.junit.Test
import pt.isel.ls.api.ActivityRoutes.ActivityCreation
import pt.isel.ls.api.ActivityRoutes.ActivityIdResponse
import pt.isel.ls.api.utils.postRequest
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.guestUser

class ActivitiesIntegrationTests {
    private val activityPath = "/api/activity/"
    // create activityServices
    val activityServices = ActivityServices(ActivityDataMemRepository())
    val userServices = UserServices(UserDataMemRepository(guestUser))
    val sportsServices = SportsServices(SportDataMemRepository())
    private val backend = getApiRoutes(Activity(activityServices, sportsServices, userServices))

    @Test
    fun `try to create an activity without the date`(){

    }
    @Test
    fun `try to create an activity without the duration`(){

    }
    @Test
    fun `try to create an activity without the sportId`(){

    }

    @Test
    fun `try to create an activity without the rid works`(){

    }

    @Test
    fun `create an activity sucessfuly`(){

    }



}