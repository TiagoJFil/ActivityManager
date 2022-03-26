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
    private val activitiesServices = ActivityServices(ActivityDataMemRepository(), UserDataMemRepository(guestUser))
    private val sportServicesTest = SportsServices(SportDataMemRepository())

    @Test
    fun `try to create an activity with a blank duration`(){
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity("123","123"," ","2002-12-31","123") }
    }
    @Test
    fun `try to create an activity with a blank date`(){
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity("123","123","02:16:32.993"," ","123") }
    }
    @Test
    fun `try to create an activity with the wrong date format`(){
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity("123","123","02:16:32.993","20-12-31","123") }
    }

    @Test
    fun `try to create an activity with the wrong duration format`(){
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity("123","123","52:16:32.993","2002-12-31","123") }
    }

    @Test
    fun `try to create an activity with a blank rid doesnt work`(){
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity("123","123","02:16:32.993","2002-12-31"," ") }
    }

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