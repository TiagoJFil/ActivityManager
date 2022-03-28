package pt.isel.ls.services

import kotlinx.datetime.toLocalDate
import org.junit.Test
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActivitiesServicesTest {
    private val sportRepo = SportDataMemRepository()
    private val activitiesServices =
        ActivityServices(ActivityDataMemRepository(), UserDataMemRepository(guestUser), sportRepo)
    private val sportServicesTest =
        SportsServices(sportRepo)

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
    fun `try to create an activity without a rid works`(){
        activitiesServices.createActivity("123","123","02:16:32.993","2002-12-31",null) //doesnt throw
    }

    @Test
    fun `get an activity by id that doesnt exist throws an error`(){
        assertFailsWith<ResourceNotFound> { activitiesServices.getActivity("312") }
    }

    @Test
    fun `get an activity by id with a blank id throws error `(){
        assertFailsWith<InvalidParameter> { activitiesServices.getActivity(" ") }
    }

    @Test
    fun `get an activity by id without an argument throws error `(){
        assertFailsWith<MissingParameter> { activitiesServices.getActivity(null) }
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

    @Test
    fun `delete an activity`(){
        val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)
        val activityID =
            activitiesServices.createActivity(guestUser.id, sportID, "02:10:32.123", "2002-05-20", "123")

        activitiesServices.deleteActivity(guestUser.id, activityID, sportID)

        val activities = activitiesServices.getActivities(sportID, "ascending", null, null)

        assertEquals(emptyList(), activities)
    }

    @Test
    fun `delete an activity with a user that didn't create it throws Unauthenticated`(){
        val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)
        val activityID =
            activitiesServices.createActivity(guestUser.id, sportID, "02:10:32.123", "2002-05-20", "123")

        assertFailsWith<UnauthenticatedError> {
            activitiesServices.deleteActivity("RANDOM_USER", activityID, sportID)
        }
    }

    @Test
    fun `delete an activity with a sport that doesn't exist throws resource not found`(){
        val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)
        val activityID =
            activitiesServices.createActivity(guestUser.id, sportID, "02:10:32.123", "2002-05-20", "123")

        assertFailsWith<ResourceNotFound> {
            activitiesServices.deleteActivity(guestUser.id, activityID, "1111111")
        }
    }

    @Test
    fun `delete an activity with an activity that doesn't exist throws resource not found`(){
        val sportID = sportServicesTest.createSport(guestUser.id, "Futebol", null)

        activitiesServices.createActivity(guestUser.id, sportID, "02:10:32.123", "2002-05-20", "123")

        assertFailsWith<ResourceNotFound> {
            activitiesServices.deleteActivity(guestUser.id, "RANDOM_ACTIVITY", sportID)
        }
    }


}