package pt.isel.ls.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.junit.After
import org.junit.Test
import pt.isel.ls.api.ActivityRoutes.ActivityInput
import pt.isel.ls.api.ActivityRoutes.ActivityListOutput
import pt.isel.ls.api.RouteRoutes.RouteInput
import pt.isel.ls.api.SportRoutes.SportInput
import pt.isel.ls.api.UserRoutes.UserInput
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
import pt.isel.ls.api.utils.expectForbidden
import pt.isel.ls.api.utils.expectNoContent
import pt.isel.ls.api.utils.expectNotFound
import pt.isel.ls.api.utils.expectOK
import pt.isel.ls.api.utils.expectUnauthorized
import pt.isel.ls.api.utils.getRequest
import pt.isel.ls.api.utils.postRequest
import pt.isel.ls.api.utils.putRequest
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testActivity
import pt.isel.ls.config.testRoute
import pt.isel.ls.config.testSport
import pt.isel.ls.service.dto.ActivityDTO
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.service.dto.UserDTO
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.service.transactions.InMemoryTransactionScope
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
        InMemoryTransactionScope.reset()
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
            RouteInput(
                "Lisboa",
                "Loures",
                20.0F
            )
        ).routeID
        val date = "2002-05-20"

        testClient.createActivity(
            ActivityInput("05:10:32.123", "2002-12-31", routeID.toString()), sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityInput("02:10:32.123", date, testRoute.id.toString()),
            sportID
        ).activityID

        val activityID3 = testClient.createActivity(
            ActivityInput("03:10:32.123", date, testRoute.id.toString()),
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
        val body = ActivityInput("02:16:32.993", null, testRoute.id.toString())
        postRequest<ActivityInput, HttpError>(
            testClient,
            SPORT_ACTIVITY_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }
    @Test
    fun `try to create an activity without the duration`() {
        val body = ActivityInput("02:16:32.993", null, testRoute.id.toString())
        postRequest<ActivityInput, HttpError>(
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
        val body = ActivityInput("02:16:32.993", "2020-01-01", testRoute.id.toString())
        postRequest<ActivityInput, HttpError>(
            testClient,
            "${ACTIVITY_PATH}$sportID/activities",
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectNotFound
        )
    }

    @Test
    fun `create an activity without the rid`() {
        val body = ActivityInput("02:16:32.993", "2020-01-01", null)
        testClient.createActivity(body, testSport.id)
    }

    @Test
    fun `create an activity sucessfuly`() {
        val body = ActivityInput("02:16:32.993", "2020-01-01", testRoute.id.toString())
        testClient.createActivity(body, testSport.id)
    }

    @Test
    fun `get the activities of a user that doesn't exist`() {
        getRequest<HttpError>(testClient, "${USER_PATH}12313/activities", Response::expectNotFound)
    }

    @Test
    fun `get a list of activities by sport ascending and descending`() {
        val sportID = testClient.createSport(SportInput("Teste", "descricao")).sportID

        val activityID1 = testClient.createActivity(
            ActivityInput("05:10:32.123", "2002-12-31", testRoute.id.toString()),
            sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityInput("02:10:32.123", "2002-12-30", testRoute.id.toString()),
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
            ActivityInput("05:10:32.123", "2002-12-31", testRoute.id.toString()), sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityInput("02:10:32.123", date, testRoute.id.toString()),
            sportID
        ).activityID

        val activityID3 = testClient.createActivity(
            ActivityInput("03:10:32.123", date, testRoute.id.toString()),
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
            ActivityInput("05:10:32.123", "2002-12-31", testRoute.id.toString()), sportID
        ).activityID

        val activityID2 = testClient.createActivity(
            ActivityInput("02:10:32.123", date, testRoute.id.toString()),
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
        val notOwner = testClient.createUser(UserInput("Joao", "joaosousa@gmail.com", "ta feito entao"))
        deleteActivity(testSport.id, testActivity.id, notOwner.authToken).expectForbidden()
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

        val userInfo2 = testClient.createUser(UserInput("Joao", "joao@email.com", "easy"))
        val user2 = getRequest<UserDTO>(testClient, "$USER_PATH${userInfo2.id}", Response::expectOK)
        testClient.createActivity(ActivityInput("00:04:00.000", "2002-05-20", routeId.toString()), sportID, userInfo2.authToken)

        val userInfo3 = testClient.createUser(UserInput("Miguel", "miguel@email.com", "easy"))
        val user3 = getRequest<UserDTO>(testClient, "$USER_PATH${userInfo3.id}", Response::expectOK)
        testClient.createActivity(ActivityInput("00:15:00.000", "2002-05-20", routeId.toString()), sportID, userInfo3.authToken)

        val userListOutput =
            getRequest<UserListOutput>(testClient, "/api/sports/$sportID/users?rid=$routeId&limit=100000", Response::expectOK)
        assertEquals(listOf(user2, guestUser.toDTO(), user3), userListOutput.users)
    }

    @Test
    fun `Get list of activites returns a list with the test user`() {
        val activities = getRequest<ActivityListOutput>(testClient, "/api/activities", Response::expectOK)
        assertEquals(listOf(testActivity.toDTO()), activities.activities)
    }

    @Test
    fun `Delete activities sucessfully`() {
        val body = ActivityInput("02:16:32.993", "2020-01-01", testRoute.id.toString())
        testClient.createActivity(body, testSport.id)

        deleteActivities("0,1", GUEST_TOKEN).expectNoContent()
    }

    @Test
    fun `Update all the parameters of the activity successfully`() {
        val rid = testClient.createRoute(RouteInput("a", "b", 20.0F)).routeID
        val body = ActivityInput("03:16:32.993", "2022-01-01", rid.toString())
        val aid = testClient.createActivity(body, testSport.id).activityID
        val updateBody = ActivityInput("05:16:32.993", "2015-01-02", testRoute.id.toString())

        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/$aid",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/$aid", Response::expectOK)
        val expected = ActivityDTO(aid, "2015-01-02", "05:16:32.993", testSport.id, testRoute.id, guestUser.id)
        assertEquals(expected, activity)
    }

    @Test
    fun `Update duration of the activity successfully`() {

        val updateBody = ActivityInput("12:16:32.893", null, null)
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            Response::expectOK
        )
        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            "12:16:32.893",
            testActivity.sport,
            testActivity.route,
            testActivity.user
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `Update date of the activity successfully`() {

        val updateBody = ActivityInput(null, "2000-05-19", null)
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/${testActivity.id}", Response::expectOK)

        val expected = ActivityDTO(
            testActivity.id,
            "2000-05-19",
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            testActivity.route,
            testActivity.user
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `Update route of the activity successfully`() {
        val rid = testClient.createRoute(RouteInput("a", "b", 20.0F)).routeID
        val updateBody = ActivityInput(null, null, rid.toString())
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/${testActivity.id}", Response::expectOK)

        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            rid,
            testActivity.user
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `Update route and duration of the activity successfully`() {
        val rid = testClient.createRoute(RouteInput("a", "b", 20.0F)).routeID
        val updateBody = ActivityInput("05:16:32.893", null, rid.toString())
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/${testActivity.id}", Response::expectOK)

        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            "05:16:32.893",
            testActivity.sport,
            rid,
            testActivity.user
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `Update route and date of the activity successfully`() {
        val rid = testClient.createRoute(RouteInput("a", "b", 20.0F)).routeID
        val updateBody = ActivityInput(null, "2007-05-11", rid.toString())
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/${testActivity.id}", Response::expectOK)

        val expected = ActivityDTO(
            testActivity.id,
            "2007-05-11",
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            rid,
            testActivity.user
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `Update duration and date of the activity successfully`() {
        val updateBody = ActivityInput("15:16:32.893", "2014-05-11", null)
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/${testActivity.id}", Response::expectOK)

        val expected = ActivityDTO(
            testActivity.id,
            "2014-05-11",
            "15:16:32.893",
            testActivity.sport,
            testActivity.route,
            testActivity.user
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `Update route with blank removes the route from the activity successfully`() {
        val updateBody = ActivityInput(null, null, "")
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/${testActivity.id}", Response::expectOK)

        val expected = ActivityDTO(
            testActivity.id,
            testActivity.date.toString(),
            Duration(testActivity.duration.millis).toFormat(),
            testActivity.sport,
            null,
            testActivity.user
        )
        assertEquals(expected, activity)
    }

    @Test
    fun `Updating all parameters with null keeps the old data`() {
        val updateBody = ActivityInput(null, null, null)

        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNoContent
        )

        val activity = getRequest<ActivityDTO>(testClient, "$SPORT_ACTIVITY_PATH/${testActivity.id}", Response::expectOK)

        assertEquals(testActivity.toDTO(), activity)
    }

    @Test
    fun `Update activity of another user fails`() {
        val updateBody = ActivityInput(null, null, null)
        val user = testClient.createUser(UserInput("x", "n@gmail.com", "ja acabou?"))
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            updateBody,
            authHeader(user.authToken),
            expectedStatus = Response::expectForbidden
        )
    }

    @Test
    fun `Update an activity that doesn't exist gives not found`() {
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/9679076",
            ActivityInput(),
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNotFound
        )
    }

    @Test
    fun `Update an activity with a route that doesn't exist`() {
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            ActivityInput(rid = "1578546"),
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNotFound
        )
    }

    @Test
    fun `Update an activity with an invalid duration gives bad request response`() {
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            ActivityInput(duration = "89864"),
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectBadRequest
        )
    }

    @Test
    fun `Update an activity with an invalid date gives bad request response`() {
        putRequest<ActivityInput>(
            testClient,
            "$SPORT_ACTIVITY_PATH/${testActivity.id}",
            ActivityInput(date = "2115-265-489"),
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectBadRequest
        )
    }

    private fun deleteActivity(sportID: SportID, activityID: ActivityID, token: UserToken) =
        testClient(
            Request(DELETE, activityResourceLocation(sportID, activityID))
                .header("Authorization", "Bearer $token")
        )

    private fun deleteActivities(activities: String, token: UserToken) =
        testClient(
            Request(POST, "/api/activities/deletes?activityIDs=$activities")
                .header("Authorization", "Bearer $token")
        )
}
