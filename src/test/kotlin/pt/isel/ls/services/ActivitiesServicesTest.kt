package pt.isel.ls.services

import org.junit.After
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.services.dto.ActivityDTO
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.utils.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActivitiesServicesTest {

    private var activitiesServices = TEST_ENV.activityServices

    @After
    fun tearDown() {
        activitiesServices = TEST_ENV.activityServices
    }

    @Test
    fun `try to create an activity with a blank duration`(){
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity(GUEST_TOKEN,"123"," ","2002-12-31","123") }
    }
    @Test
    fun `try to create an activity with a blank date`(){
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity(GUEST_TOKEN,"123","02:16:32.993"," ","123") }
    }
    @Test
    fun `try to create an activity with the wrong date format`(){
        assertFailsWith<InvalidParameter> {
            activitiesServices.createActivity(
                    GUEST_TOKEN,
                    testSport.id,
                    "02:16:32.993",
                    "2020-12-76",
                    testRoute.id)
        }
    }

    @Test
    fun `try to create an activity with the wrong duration format`(){
        assertFailsWith<InvalidParameter> {
            activitiesServices.createActivity(
                    token=GUEST_TOKEN,
                    sportID = testSport.id,
                    "INVALIDO",
                    "2002-12-31",
                    rid= testRoute.id
            )
        }
    }

    @Test
    fun `try to create an activity with a blank rid throws invalid parameter`(){
        assertFailsWith<InvalidParameter> {
            activitiesServices.createActivity(GUEST_TOKEN, testSport.id,"02:16:32.993","2002-12-31"," ")
        }
    }

    @Test
    fun `try to create an activity without a rid works`(){
        activitiesServices.createActivity(GUEST_TOKEN, testSport.id,"02:16:32.993","2002-12-31",null)
        //doesn't throw
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
        val sportID = testSport.id
        val activityID =
            activitiesServices.createActivity(GUEST_TOKEN, sportID, "02:10:32.123", "2002-05-20", testRoute.id)

        val activitiesExpected = listOf(
                testActivity.toDTO(),
                ActivityDTO(activityID, "2002-05-20", "02:10:32.123", sportID, testRoute.id, guestUser.id)
        )

        val sut = activitiesServices.getActivitiesByUser(guestUser.id)
        assertEquals(activitiesExpected, sut)
    }

    @Test
    fun `get an activity with an invalid user throws resource not found`(){
        assertFailsWith<ResourceNotFound> {
            activitiesServices.getActivitiesByUser("invalido")
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
        val sportID = testSport.id

        val activityID =
            activitiesServices.createActivity(GUEST_TOKEN, sportID, "02:10:32.123", "2002-05-20", testRoute.id)

        val activitiesExpected = listOf(
                testActivity.toDTO(),
                ActivityDTO(activityID, "2002-05-20", "02:10:32.123", sportID, testRoute.id, guestUser.id)
        )


        val activities = activitiesServices.getActivities(sportID, "ascending", null, null)

        assertEquals(activitiesExpected, activities)
    }

    @Test
    fun `get activities of a sport with a blank date throws invalid parameter`(){
        assertFailsWith<InvalidParameter> {
            val sportID = testSport.id
            activitiesServices.getActivities(sportID, "ascending", "", null)
        }
    }

    @Test
    fun `get activities of a sport with a blank route id throws invalid parameter`(){
        assertFailsWith<InvalidParameter> {
            val sportID = testSport.id
            activitiesServices.getActivities(sportID, "ascending", null, "")
        }
    }

    @Test
    fun `delete an activity`(){
        val sportID = testSport.id

        activitiesServices.deleteActivity(GUEST_TOKEN, testActivity.id, sportID)

        val activities = activitiesServices.getActivities(sportID, "ascending", null, null)

        assertEquals(emptyList(), activities)
    }

    @Test
    fun `delete an activity with a user that didn't create it throws Unauthenticated`(){
        val sportID = testSport.id
        val activityID =
            activitiesServices.createActivity(GUEST_TOKEN, sportID, "02:10:32.123", "2002-05-20", testRoute.id)

        assertFailsWith<UnauthenticatedError> {
            activitiesServices.deleteActivity("RANDOM_USER", activityID, sportID)
        }
    }

    @Test
    fun `delete an activity with a sport that doesn't exist throws resource not found`(){
        val sportID = testSport.id
        val activityID =
            activitiesServices.createActivity(GUEST_TOKEN, sportID, "02:10:32.123", "2002-05-20", testRoute.id)

        assertFailsWith<ResourceNotFound> {
            activitiesServices.deleteActivity(GUEST_TOKEN, activityID, "1111111")
        }
    }

    @Test
    fun `delete an activity that doesn't exist throws resource not found`(){
        val sportID = testSport.id
        assertFailsWith<ResourceNotFound> {
            activitiesServices.deleteActivity(GUEST_TOKEN, "RANDOM_ACTIVITY", sportID)
        }
    }


}