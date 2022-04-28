package pt.isel.ls.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Request
import org.http4k.core.Response
import org.junit.After
import org.junit.Test
import pt.isel.ls.api.ActivityRoutes.ActivityCreationInput
import pt.isel.ls.api.ActivityRoutes.ActivityListOutput
import pt.isel.ls.api.RouteRoutes.RouteCreationInput
import pt.isel.ls.api.SportRoutes.SportCreationInput
import pt.isel.ls.api.UserRoutes.UserCreationInput
import pt.isel.ls.api.UserRoutes.UserListOutput
import pt.isel.ls.api.utils.ACTIVITY_PATH
import pt.isel.ls.api.utils.SPORT_PATH
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.api.utils.USER_PATH
import pt.isel.ls.api.utils.authHeader
import pt.isel.ls.api.utils.createActivity
import pt.isel.ls.api.utils.createRoute
import pt.isel.ls.api.utils.createSport
import pt.isel.ls.api.utils.createUser
import pt.isel.ls.api.utils.expectBadRequest
import pt.isel.ls.api.utils.expectNoContent
import pt.isel.ls.api.utils.expectNotFound
import pt.isel.ls.api.utils.expectOK
import pt.isel.ls.api.utils.expectUnauthorized
import pt.isel.ls.api.utils.getRequest
import pt.isel.ls.api.utils.postRequest
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testActivity
import pt.isel.ls.config.testRoute
import pt.isel.ls.config.testSport
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.service.dto.UserDTO
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ActivitiesApiTests {

    private val SPORT_ACTIVITY_PATH = "${ACTIVITY_PATH}${testSport.id}/activities"
    private val USER_ACTIVITY_PATH = "${USER_PATH}${guestUser.id}/activities"

    private fun activityResourceLocation(sportID: SportID, activityID: ActivityID) =
        "$ACTIVITY_PATH$sportID/activities/$activityID"

    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV)) // Resets the client resetting the database
    }

    @Test
    fun `get a user's activities`() {
        val userActivities = getRequest<ActivityListOutput>(testClient, USER_ACTIVITY_PATH, Response::expectOK).activities

        val expectedList = listOf<ActivityDTO>(testActivity.toDTO())
        assertEquals(expectedList, userActivities)
    }

    @Test
    fun `get a list of activities by sport filtered by route`() {
        val sportID = testSport.id
        val routeID = testClient.createRoute(
            RouteCreationInput(
                "Lisboa",
                "Loures",
                20.0
            )
        ).routeID
        val date = "2002-05-20"

        testClient.createActivity(
            ActivityCreationInput("05:10:32.123", "2002-12-31", routeID.toString()), sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityCreationInput("02:10:32.123", date, testRoute.id.toString()),
            sportID
        ).activityID

        val activityID3 = testClient.createActivity(
            ActivityCreationInput("03:10:32.123", date, testRoute.id.toString()),
            sportID
        ).activityID

        val activitiesList = getRequest<ActivityListOutput>(
            testClient,
            "$SPORT_ACTIVITY_PATH?rid=${testRoute.id}",
            Response::expectOK
        ).activities

        val activity1 =
            ActivityDTO(activityID2, date, "02:10:32.123", sportID, testRoute.id, guestUser.id)

        val activity2 =
            ActivityDTO(activityID3, date, "03:10:32.123", sportID, testRoute.id, guestUser.id)

        val expectedActivitiesList = listOf<ActivityDTO>(testActivity.toDTO(), activity1, activity2)
            .sortedBy { it.duration }

        assertContentEquals(expectedActivitiesList, activitiesList)
    }

    @Test
    fun `try to create an activity without the date`() {
        val body = ActivityCreationInput("02:16:32.993", null, testRoute.id.toString())
        postRequest<ActivityCreationInput, HttpError>(
            testClient,
            SPORT_ACTIVITY_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }
    @Test
    fun `try to create an activity without the duration`() {
        val body = ActivityCreationInput("02:16:32.993", null, testRoute.id.toString())
        postRequest<ActivityCreationInput, HttpError>(
            testClient,
            SPORT_ACTIVITY_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }
    @Test
    fun `try to create an activity with an invalid sportId`() {
        val sportID = "123123"
        val body = ActivityCreationInput("02:16:32.993", "2020-01-01", testRoute.id.toString())
        postRequest<ActivityCreationInput, HttpError>(
            testClient,
            "${ACTIVITY_PATH}$sportID/activities",
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectNotFound
        )
    }

    @Test
    fun `create an activity without the rid`() {
        val body = ActivityCreationInput("02:16:32.993", "2020-01-01", null)
        testClient.createActivity(body, testSport.id)
    }

    @Test
    fun `create an activity sucessfuly`() {
        val body = ActivityCreationInput("02:16:32.993", "2020-01-01", testRoute.id.toString())
        testClient.createActivity(body, testSport.id)
    }

    @Test
    fun `get the activities of a user that doesn't exist`() {
        getRequest<HttpError>(testClient, "${USER_PATH}12313/activities", Response::expectNotFound)
    }

    @Test
    fun `8get a list of activities by sport ascending and descending`() {
        val sportID = testClient.createSport(SportCreationInput("Teste", "descricao")).sportID

        val activityID1 = testClient.createActivity(
            ActivityCreationInput("05:10:32.123", "2002-12-31", testRoute.id.toString()),
            sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityCreationInput("02:10:32.123", "2002-12-30", testRoute.id.toString()),
            sportID
        ).activityID

        val activitiesListDescending = getRequest<ActivityListOutput>(
            testClient,
            "$SPORT_PATH$sportID/activities?orderBy=descending",
            Response::expectOK
        ).activities

        val activitiesListAscending = getRequest<ActivityListOutput>(
            testClient,
            "$SPORT_PATH$sportID/activities?orderBy=ascending",
            Response::expectOK
        ).activities

        val activity1 =
            ActivityDTO(activityID1, "2002-12-31", "05:10:32.123", sportID, testRoute.id, guestUser.id)

        val activity2 =
            ActivityDTO(activityID2, "2002-12-30", "02:10:32.123", sportID, testRoute.id, guestUser.id)

        val listExpectedAscending = listOf(activity2, activity1)
        val listExpectedDescending = listOf(activity1, activity2)

        assertContentEquals(listExpectedAscending, activitiesListAscending)
        assertContentEquals(listExpectedDescending, activitiesListDescending)
    }

    @Test
    fun `get a list of activities by sport filtered by date`() {
        val sportID = testSport.id
        val date = "2002-05-20"

        testClient.createActivity(
            ActivityCreationInput("05:10:32.123", "2002-12-31", testRoute.id.toString()), sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityCreationInput("02:10:32.123", date, testRoute.id.toString()),
            sportID
        ).activityID

        val activityID3 = testClient.createActivity(
            ActivityCreationInput("03:10:32.123", date, testRoute.id.toString()),
            sportID
        ).activityID

        val activitiesList = getRequest<ActivityListOutput>(
            testClient,
            "$SPORT_ACTIVITY_PATH?date=2002-05-20",
            Response::expectOK
        ).activities

        val activity1 =
            ActivityDTO(activityID2, date, "02:10:32.123", sportID, testRoute.id, guestUser.id)

        val activity2 =
            ActivityDTO(activityID3, date, "03:10:32.123", sportID, testRoute.id, guestUser.id)

        val expectedActivitiesList = listOf<ActivityDTO>(activity1, activity2)

        assertContentEquals(expectedActivitiesList, activitiesList)
    }

    @Test
    fun `get a list of activities by sport filtered by date and route id`() {
        val sportID = testSport.id
        val date = testActivity.date.toString()
        testClient.createActivity(
            ActivityCreationInput("05:10:32.123", "2002-12-31", testRoute.id.toString()), sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityCreationInput("02:10:32.123", date, testRoute.id.toString()),
            sportID
        ).activityID

        val activitiesList = getRequest<ActivityListOutput>(
            testClient,
            "$SPORT_ACTIVITY_PATH?rid=${testRoute.id}&date=$date",
            Response::expectOK
        ).activities

        val activity1 =
            ActivityDTO(activityID2, date, "02:10:32.123", sportID, testRoute.id, guestUser.id)

        val expectedActivitiesList = listOf<ActivityDTO>(testActivity.toDTO(), activity1).sortedBy { it.duration }
        assertContentEquals(expectedActivitiesList, activitiesList)
    }

    @Test
    fun `get a list of activities by sport filtered by inexistent date and route id gives empty list`() {
        val activitiesList = getRequest<ActivityListOutput>(
            testClient,
            "$SPORT_ACTIVITY_PATH?rid=${testRoute.id}&date=2002-05-20",
            Response::expectOK
        ).activities

        assertContentEquals(listOf(), activitiesList)
    }

    @Test
    fun `get not found error trying to get the activities of a sport that does not exist`() {
        val sportID = "123123"
        getRequest<HttpError>(testClient, "${SPORT_PATH}$sportID/activities", Response::expectNotFound)
    }

    @Test
    fun `get invalid parameter error trying to get the activities of a sport receiving an order that doesn't exist`() {
        getRequest<HttpError>(testClient, "$SPORT_ACTIVITY_PATH?orderBy=invalido", Response::expectBadRequest)
    }

    @Test
    fun `delete an activity successfully`() {
        deleteActivity(testSport.id, testActivity.id, GUEST_TOKEN).expectNoContent()
    }

    @Test
    fun `try to delete an activity of a sport that does not exist gives 404`() {
        val sportID = 123123
        deleteActivity(sportID, testActivity.id, GUEST_TOKEN).expectNotFound()
    }

    @Test
    fun `try to delete an activity through a user that didn't create the activity gives 401`() {
        val notOwner = testClient.createUser(UserCreationInput("Joao", "joaosousa@gmail.com"))
        deleteActivity(testSport.id, testActivity.id, notOwner.authToken).expectUnauthorized()
    }

    @Test
    fun `try to delete an activity that does not exist gives 404`() {
        val activityID = 1231231

        deleteActivity(testSport.id, activityID, GUEST_TOKEN).expectNotFound()
    }

    @Test
    fun `try to delete an activity with an invalid token gives 401`() {
        deleteActivity(testSport.id, testActivity.id, "invalid_token").expectUnauthorized()
    }

    @Test
    fun `get invalid parameter error trying to get the activities of a sport receiving an invalid date`() {
        getRequest<HttpError>(testClient, "$SPORT_ACTIVITY_PATH?date=invalido", Response::expectBadRequest)
    }
    @Test
    fun `get an activity that does not exist`() {
        getRequest<HttpError>(
            testClient,
            activityResourceLocation(testSport.id, 123123),
            Response::expectNotFound
        )
    }

    @Test
    fun `get an activity that does exist`() {
        getRequest<ActivityDTO>(testClient, activityResourceLocation(testSport.id, testActivity.id), Response::expectOK)
    }

    @Test
    fun `get users of an activity with a non existing rid and sid`() {

        getRequest<HttpError>(
            testClient,
            "/api/sports/21/users?rid=12",
            Response::expectNotFound
        )
    }

    @Test
    fun `get users of an activity with an invalid rid`() {
        val sid = testSport.id.toString()

        getRequest<HttpError>(
            testClient,
            "/api/sports/$sid/users?rid=   d ",
            Response::expectBadRequest
        )
    }

    @Test
    fun `get users of an activity sucessfully`() {
        val sid = testSport.id.toString()
        val rid = testRoute.id.toString()
        val userListOutput = getRequest<UserListOutput>(
            testClient,
            "/api/sports/$sid/users?rid=$rid",
            Response::expectOK
        )
        assertEquals(listOf(guestUser.toDTO()), userListOutput.users)
    }

    @Test
    fun `get users of an activity sucessfully and ordered`() {
        val sportID = testSport.id
        val routeId = testRoute.id

        val userInfo2 = testClient.createUser(UserCreationInput("Joao", "joao@email.com"))
        val user2 = getRequest<UserDTO>(testClient, "$USER_PATH${userInfo2.id}", Response::expectOK)
        testClient.createActivity(ActivityCreationInput("00:04:00.000", "2002-05-20", routeId.toString()), sportID, userInfo2.authToken)

        val userInfo3 = testClient.createUser(UserCreationInput("Miguel", "miguel@email.com"))
        val user3 = getRequest<UserDTO>(testClient, "$USER_PATH${userInfo3.id}", Response::expectOK)
        testClient.createActivity(ActivityCreationInput("00:15:00.000", "2002-05-20", routeId.toString()), sportID, userInfo3.authToken)

        val userListOutput =
            getRequest<UserListOutput>(testClient, "/api/sports/$sportID/users?rid=$routeId&limit=100000", Response::expectOK)
        assertEquals(listOf(user2, guestUser.toDTO(), user3), userListOutput.users)
    }

    private fun deleteActivity(sportID: SportID, activityID: ActivityID, token: UserToken) =
        testClient(
            Request(DELETE, activityResourceLocation(sportID, activityID))
                .header("Authorization", "Bearer $token")
        )
}
