package pt.isel.ls.service

import org.junit.After
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testActivity
import pt.isel.ls.config.testRoute
import pt.isel.ls.config.testSport
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.repository.transactions.InMemoryTransactionScope
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActivitiesServicesTest {

    private val env = TEST_ENV
    private var activitiesServices = env.activityServices
    private var routeServices = env.routeServices
    private var userServices = env.userServices

    @After
    fun tearDown() {
        InMemoryTransactionScope.reset()
    }

    @Test
    fun `try to create an activity with a blank duration`() {
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity(GUEST_TOKEN, "123", " ", "2002-12-31", "123") }
    }
    @Test
    fun `try to create an activity with a blank date`() {
        assertFailsWith<InvalidParameter> { activitiesServices.createActivity(GUEST_TOKEN, "123", "02:16:32.993", " ", "123") }
    }
    @Test
    fun `try to create an activity with the wrong date format`() {
        assertFailsWith<InvalidParameter> {
            activitiesServices.createActivity(
                GUEST_TOKEN,
                testSport.id.toString(),
                "02:16:32.993",
                "2020-12-76",
                testRoute.id.toString()
            )
        }
    }

    @Test
    fun `try to create an activity with the wrong duration format`() {
        assertFailsWith<InvalidParameter> {
            activitiesServices.createActivity(
                token = GUEST_TOKEN,
                sportID = testSport.id.toString(),
                "INVALIDO",
                "2002-12-31",
                rid = testRoute.id.toString()
            )
        }
    }

    @Test
    fun `try to create an activity with a blank rid throws invalid parameter`() {
        assertFailsWith<InvalidParameter> {
            activitiesServices.createActivity(GUEST_TOKEN, testSport.id.toString(), "02:16:32.993", "2002-12-31", " ")
        }
    }

    @Test
    fun `try to create an activity without a rid works`() {
        activitiesServices.createActivity(GUEST_TOKEN, testSport.id.toString(), "02:16:32.993", "2002-12-31", null)
        // doesn't throw
    }

    @Test
    fun `get an activity by id that doesnt exist throws an error`() {
        assertFailsWith<ResourceNotFound> { activitiesServices.getActivity("312", "0") }
    }

    @Test
    fun `get an activity by id with a blank id throws error `() {
        assertFailsWith<InvalidParameter> { activitiesServices.getActivity(" ", "0") }
    }

    @Test
    fun `get an activity by id without an argument throws error `() {
        assertFailsWith<MissingParameter> { activitiesServices.getActivity(null, "0") }
    }

    @Test
    fun `get an activity by user successfully`() {
        val sportID = testSport.id
        val activityID =
            activitiesServices.createActivity(
                GUEST_TOKEN,
                sportID.toString(),
                "02:10:32.123",
                "2002-05-20",
                testRoute.id.toString()
            )

        val activitiesExpected = listOf(
            testActivity.toDTO(),
            ActivityDTO(activityID, "2002-05-20", "02:10:32.123", sportID, testRoute.id, guestUser.id)
        )

        val sut = activitiesServices.getActivitiesByUser(guestUser.id.toString())
        assertEquals(activitiesExpected, sut)
    }

    @Test
    fun `get an activity with an invalid user throws resource not found`() {
        assertFailsWith<ResourceNotFound> {
            activitiesServices.getActivitiesByUser("12314")
        }
    }

    @Test
    fun `get a user id without passing the id throws missing parameter`() {
        assertFailsWith<MissingParameter> {
            activitiesServices.getActivitiesByUser(null)
        }
    }

    @Test
    fun `get activities of a sport`() {
        val sportID = testSport.id

        val activityID =
            activitiesServices.createActivity(
                GUEST_TOKEN,
                sportID.toString(),
                "02:10:32.123",
                "2002-05-20",
                testRoute.id.toString()
            )

        val activitiesExpected = listOf(
            testActivity.toDTO(),
            ActivityDTO(activityID, "2002-05-20", "02:10:32.123", sportID, testRoute.id, guestUser.id)
        )

        val activities = activitiesServices.getActivities(
            sportID.toString(),
            "ascending",
            null,
            null,
            PaginationInfo(10, 0)
        )

        assertEquals(activitiesExpected, activities)
    }

    @Test
    fun `get activities of a sport with a blank date throws invalid parameter`() {
        assertFailsWith<InvalidParameter> {
            val sportID = testSport.id.toString()
            activitiesServices.getActivities(sportID, "ascending", "", null, PaginationInfo(10, 0))
        }
    }

    @Test
    fun `get activities of a sport with a blank route id throws invalid parameter`() {
        assertFailsWith<InvalidParameter> {
            val sportID = testSport.id.toString()
            activitiesServices.getActivities(sportID, "ascending", null, "", PaginationInfo(10, 0))
        }
    }

    @Test
    fun `delete an activity`() {
        val sportID = testSport.id.toString()

        activitiesServices.deleteActivity(GUEST_TOKEN, testActivity.id.toString(), sportID)

        val activities = activitiesServices.getActivities(sportID, "ascending", null, null, PaginationInfo(10, 0))

        assertEquals(emptyList(), activities)
    }

    @Test
    fun `delete an activity with a user that didn't create it throws Unauthenticated`() {
        val sportID = testSport.id.toString()
        val activityID =
            activitiesServices.createActivity(GUEST_TOKEN, sportID, "02:10:32.123", "2002-05-20", testRoute.id.toString())

        assertFailsWith<UnauthenticatedError> {
            activitiesServices.deleteActivity("RANDOM_USER", activityID.toString(), sportID)
        }
    }

    @Test
    fun `delete an activity with a sport that doesn't exist throws resource not found`() {
        val sportID = testSport.id.toString()
        val activityID =
            activitiesServices.createActivity(GUEST_TOKEN, sportID, "02:10:32.123", "2002-05-20", testRoute.id.toString())

        assertFailsWith<ResourceNotFound> {
            activitiesServices.deleteActivity(GUEST_TOKEN, activityID.toString(), "1111111")
        }
    }

    @Test
    fun `delete an activity that doesn't exist throws resource not found`() {
        val sportID = testSport.id.toString()
        assertFailsWith<ResourceNotFound> {
            activitiesServices.deleteActivity(GUEST_TOKEN, "12343124", sportID)
        }
    }

    @Test
    fun `get users of an activity with a valid rid and sid`() {
        val sportID = testSport.id.toString()
        val routeId = testRoute.id.toString()
        activitiesServices.createActivity(GUEST_TOKEN, sportID, "02:10:32.123", "2002-05-20", routeId)
        val users = activitiesServices.getUsersByActivity(sportID, routeId, PaginationInfo(10, 0))
        assertEquals(listOf(guestUser.toDTO()), users)
    }

    @Test
    fun `get all activities returns an the test activity when there are no activities`() {
        val activities = activitiesServices.getAllActivities(PaginationInfo(10, 0))
        assertEquals(listOf(testActivity.toDTO()), activities)
    }

    @Test
    fun `get all activities returns a list with existing activities`() {
        val aid = activitiesServices.createActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            "02:10:32.123",
            "2002-05-20",
            testRoute.id.toString()
        )
        val activity = activitiesServices.getActivity(aid.toString(), "0")
        val activities = activitiesServices.getAllActivities(PaginationInfo(10, 0))
        assertEquals(listOf(testActivity.toDTO(), activity), activities)
    }

    @Test
    fun `try to delete activities that dont exist`() {
        assertFailsWith<ResourceNotFound> {
            activitiesServices.deleteActivities(GUEST_TOKEN, "1,4,2,3,6")
        }
    }

    @Test
    fun `update all the properties of the activity successfully`() {
        val rid = routeServices.createRoute(GUEST_TOKEN, "a", "b", 220.0F)

        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            "05:10:32.123",
            "2012-05-20",
            rid.toString()
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            "2012-05-20",
            "05:10:32.123",
            testActivity.sport,
            rid,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `update date of the activity successfully`() {

        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            null,
            "2016-07-20",
            null
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            "2016-07-20",
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            testActivity.route,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `update route of the activity successfully`() {
        val rid = routeServices.createRoute(GUEST_TOKEN, "a", "b", 220.0F)
        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            null,
            null,
            rid.toString()
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            rid,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `update duration of the activity successfully`() {
        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            "15:10:32.123",
            null,
            null
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            "15:10:32.123",
            testActivity.sport,
            testActivity.route,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `update duration and route of the activity successfully`() {
        val rid = routeServices.createRoute(GUEST_TOKEN, "a", "b", 220.0F)
        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            "20:10:32.123",
            null,
            rid.toString()
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            "20:10:32.123",
            testActivity.sport,
            rid,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `update duration and date of the activity successfully`() {
        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            "20:10:32.123",
            "2018-08-20",
            null
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            "2018-08-20",
            "20:10:32.123",
            testActivity.sport,
            testActivity.route,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `update date and route of the activity successfully`() {
        val rid = routeServices.createRoute(GUEST_TOKEN, "a", "b", 220.0F)
        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            null,
            "2019-08-20",
            rid.toString()
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            "2019-08-20",
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            rid,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `update with blank route removes the route successfully`() {
        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            null,
            null,
            ""
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            null,
            guestUser.id
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `updating nothing keeps the previous activity`() {
        activitiesServices.updateActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            testActivity.id.toString(),
            null,
            null,
            null
        )

        val activity = activitiesServices.getActivity(testActivity.id.toString(), testActivity.sport.toString())
        assertEquals(testActivity.toDTO(), activity)
    }

    @Test
    fun `update activity with user that didn't create it throws UnauthenticatedError`() {
        val (token, _) = userServices.createUser("John", "john@email.com")

        assertFailsWith<AuthorizationError> {
            activitiesServices.updateActivity(
                token,
                testSport.id.toString(),
                testActivity.id.toString(),
                null,
                null,
                null
            )
        }
    }

    @Test
    fun `update activity that does not exists`() {
        assertFailsWith<ResourceNotFound> {
            activitiesServices.updateActivity(
                GUEST_TOKEN,
                testSport.id.toString(),
                "999986",
                null,
                null,
                null
            )
        }
    }

    @Test
    fun `update activity with invalid date fails`() {
        assertFailsWith<InvalidParameter> {
            activitiesServices.updateActivity(
                GUEST_TOKEN,
                testSport.id.toString(),
                testActivity.id.toString(),
                null,
                "2562-001-689",
                null
            )
        }
    }

    @Test
    fun `update activity with invalid duration fails`() {
        assertFailsWith<InvalidParameter> {
            activitiesServices.updateActivity(
                GUEST_TOKEN,
                testSport.id.toString(),
                testActivity.id.toString(),
                "1234566",
                null,
                null
            )
        }
    }

    @Test
    fun `update activity with a route that doesn't exist fails`() {
        assertFailsWith<ResourceNotFound> {
            activitiesServices.updateActivity(
                GUEST_TOKEN,
                testSport.id.toString(),
                testActivity.id.toString(),
                null,
                null,
                "9999898"
            )
        }
    }

    // this test should not delete the activities if any goes wrong
    @Test
    fun `Delete activities doesnt work when deleting the same activity twice`() {
        val aid = activitiesServices.createActivity(
            GUEST_TOKEN,
            testSport.id.toString(),
            "02:10:32.123", "2002-05-20",
            testRoute.id.toString()
        )
        val activities = activitiesServices.getAllActivities(PaginationInfo(10, 0))
        assertFailsWith<InvalidParameter> {
            activitiesServices.deleteActivities(GUEST_TOKEN, "$aid,$aid")
        }
        val activities2 = activitiesServices.getAllActivities(PaginationInfo(10, 0))
        assertEquals(activities, activities2)
    }
}
