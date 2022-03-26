package pt.isel.ls.services

import kotlinx.datetime.toLocalDate
import org.junit.Test
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.utils.GUEST_TOKEN
import pt.isel.ls.utils.Order
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActivitiesServicesTest {
    val activitiesServices = ActivityServices(ActivityDataMemRepository(), UserDataMemRepository(guestUser))
    val sportServicesTest = SportsServices(SportDataMemRepository())

    @Test
    fun `get an activity by user successfully`(){
        val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)
        val activityID =
                activitiesServices.createActivity(guestUser.id, sportID, "02:10:32.123", "2002-05-20", "123")
        val activity =
                Activity(activityID, "2002-05-20".toLocalDate(), "02:10:32.123", sportID, "123", guestUser.id)

        val sut = activitiesServices.getActivitiesByUser(guestUser.id)
        assertEquals(listOf(activity), sut)
    }

    @Test
    fun `get an activity with an invalid user throws resource not found`(){
        assertFailsWith<ResourceNotFound> {
            activitiesServices.getActivitiesByUser("1111111")
        }
    }

    @Test
    fun `get a user id without passing the id throws missing parameter`(){
        assertFailsWith<MissingParameter> {
            activitiesServices.getActivitiesByUser(null)
        }
    }

    @Test
    fun `get activities of a sport`(){
        val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)

        val activityID =
                activitiesServices.createActivity(guestUser.id, sportID, "02:10:32.123", "2002-05-20", "123")

        val activity =
                Activity(activityID, "2002-05-20".toLocalDate(), "02:10:32.123", sportID, "123", guestUser.id)

        val activities = activitiesServices.getActivities(sportID, "ascending", null, null)

        assertEquals(listOf(activity), activities)
    }

    @Test
    fun `get activities of a sport with a blank date throws invalid parameter`(){
        assertFailsWith<InvalidParameter> {
            val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)
            activitiesServices.getActivities(sportID, "ascending", "", null)
        }
    }

    @Test
    fun `get activities of a sport with a blank route id throws invalid parameter`(){
        assertFailsWith<InvalidParameter> {
            val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)
            activitiesServices.getActivities(sportID, "ascending", null, "")
        }
    }
}