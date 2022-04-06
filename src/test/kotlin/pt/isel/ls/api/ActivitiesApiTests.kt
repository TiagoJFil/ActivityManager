package pt.isel.ls.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Request
import org.http4k.core.Response
import org.junit.After
import org.junit.Test
import pt.isel.ls.api.ActivityRoutes.ActivityCreationBody
import pt.isel.ls.api.ActivityRoutes.ActivityList
import pt.isel.ls.api.RouteRoutes.RouteCreationBody
import pt.isel.ls.api.SportRoutes.SportCreationBody
import pt.isel.ls.api.utils.*
import pt.isel.ls.config.*
import pt.isel.ls.services.dto.ActivityDTO
import pt.isel.ls.services.dto.HttpError
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserToken
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals


class ActivitiesApiTests {

    private val SPORT_ACTIVITY_PATH = "${ACTIVITY_PATH}${testSport.id}/activities"
    private val USER_ACTIVITY_PATH = "${USER_PATH}${guestUser.id}/activities"

    private fun activityResourceLocation(sportID: SportID, activityID: ActivityID)
        = "$ACTIVITY_PATH$sportID/activities/$activityID"

    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV)) // Resets the client resetting the database
    }

    @Test
    fun `get a user's activities`() {
        val userActivities = getRequest<ActivityList>(testClient, USER_ACTIVITY_PATH, Response::expectOK).activities

        val expectedList = listOf<ActivityDTO>(testActivity.toDTO())
        assertEquals(expectedList, userActivities)
    }

    @Test
    fun `get a list of activities by sport filtered by route`(){
        val sportID = testSport.id
        val routeID = testClient.createRoute(RouteCreationBody(
                "Lisboa",
                "Loures",
                20.0)
        ).routeID
        val date = "2002-05-20"

        testClient.createActivity(
                ActivityCreationBody("05:10:32.123", "2002-12-31", routeID)
                ,sportID
        ).activityID

        val activityID2 = testClient.createActivity(
                ActivityCreationBody("02:10:32.123", date , testRoute.id),
                sportID
        ).activityID

        val activityID3 = testClient.createActivity(
                ActivityCreationBody("03:10:32.123", date, testRoute.id),
                sportID
        ).activityID

        val activitiesList= getRequest<ActivityList>(
                testClient,
                "$SPORT_ACTIVITY_PATH?rid=${testRoute.id}",
                Response::expectOK
        ).activities

        val activity1 =
                ActivityDTO(activityID2, date, "02:10:32.123", sportID, testRoute.id, guestUser.id)

        val activity2 =
                ActivityDTO(activityID3, date, "03:10:32.123", sportID, testRoute.id, guestUser.id)

        val expectedActivitiesList = listOf<ActivityDTO>(testActivity.toDTO(),activity1, activity2)
                .sortedBy { it.duration }

        assertContentEquals(expectedActivitiesList, activitiesList)
    }

    @Test
    fun `try to create an activity without the date`(){
        val body = ActivityCreationBody("02:16:32.993",null , testRoute.id)
        postRequest<ActivityCreationBody, HttpError>(
                testClient,
                SPORT_ACTIVITY_PATH,
                body,
                headers = authHeader(GUEST_TOKEN),
                Response::expectBadRequest
        )
    }
    @Test
    fun `try to create an activity without the duration`(){
        val body =ActivityCreationBody("02:16:32.993",null , testRoute.id)
        postRequest<ActivityCreationBody, HttpError>(
                testClient,
                SPORT_ACTIVITY_PATH,
                body,
                headers = authHeader(GUEST_TOKEN),
                Response::expectBadRequest
        )

    }
    @Test
    fun `try to create an activity with an invalid sportId`(){
        val sportID = "INVALID"
        val body = ActivityCreationBody("02:16:32.993","2020-01-01", testRoute.id)
        postRequest<ActivityCreationBody, HttpError>(
                testClient,
                "${ACTIVITY_PATH}${sportID}/activities",
                body,
                headers = authHeader(GUEST_TOKEN),
                Response::expectNotFound
        )
    }

    @Test
    fun `create an activity without the rid`(){
        val body = ActivityCreationBody("02:16:32.993","2020-01-01", null)
        testClient.createActivity(body, testSport.id)
    }

    @Test
    fun `create an activity sucessfuly`(){
        val body = ActivityCreationBody("02:16:32.993","2020-01-01", testRoute.id)
        testClient.createActivity(body, testSport.id)
    }


    @Test
    fun `get the activities of a user that doesn't exist`() {
        getRequest<HttpError>(testClient, "${USER_PATH}invalido/activities", Response::expectNotFound)
    }


    @Test
    fun `8get a list of activities by sport ascending and descending`(){
        val sportID = testClient.createSport(SportCreationBody("Teste", "descricao")).sportID

        val activityID1 = testClient.createActivity(
                ActivityCreationBody("05:10:32.123", "2002-12-31", testRoute.id),
                sportID
        ).activityID

        val activityID2 = testClient.createActivity(
                ActivityCreationBody("02:10:32.123", "2002-12-30", testRoute.id),
                sportID
        ).activityID

        val activitiesListDescending= getRequest<ActivityList>(
                testClient,
                "$SPORT_PATH${sportID}/activities?orderBy=descending",
                Response::expectOK
        ).activities

        val activitiesListAscending= getRequest<ActivityList>(
                testClient,
                "$SPORT_PATH${sportID}/activities?orderBy=ascending",
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
    fun `get a list of activities by sport filtered by date`(){
        val sportID = testSport.id
        val date = "2002-05-20"

        testClient.createActivity(
                ActivityCreationBody("05:10:32.123", "2002-12-31", testRoute.id)
                ,sportID
        ).activityID

        val activityID2 = testClient.createActivity(
                ActivityCreationBody("02:10:32.123", date, testRoute.id),
                sportID
        ).activityID

        val activityID3 = testClient.createActivity(
                ActivityCreationBody("03:10:32.123", date, testRoute.id),
                sportID
        ).activityID

        val activitiesList= getRequest<ActivityList>(
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
                ActivityCreationBody("05:10:32.123", "2002-12-31", testRoute.id)
                ,sportID
        ).activityID

        val activityID2 = testClient.createActivity(
                ActivityCreationBody("02:10:32.123", date, testRoute.id),
                sportID
        ).activityID

        val activitiesList= getRequest<ActivityList>(
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
        val activitiesList= getRequest<ActivityList>(
                testClient,
                "${SPORT_ACTIVITY_PATH}?rid=${testRoute.id}}&date=2002-05-20",
                Response::expectOK
        ).activities

        assertContentEquals(listOf(), activitiesList)
    }

    @Test
    fun `get not found error trying to get the activities of a sport that does not exist`(){
        val sportID = "RANDOM_SPORT"
        getRequest<HttpError>(testClient, "${SPORT_PATH}${sportID}/activities", Response::expectNotFound)
    }

    @Test
    fun `get invalid parameter error trying to get the activities of a sport receiving an order that doesn't exist`(){
        getRequest<HttpError>(testClient, "${SPORT_ACTIVITY_PATH}?orderBy=invalido", Response::expectBadRequest)
    }

    @Test
    fun `delete an activity successfully`(){
        deleteActivity(testSport.id, testActivity.id, GUEST_TOKEN).expectNoContent()
    }

    @Test
    fun `try to delete an activity of a sport that does not exist gives 404`(){
        val sportID = "RANDOM_SPORT"
        deleteActivity(sportID, testActivity.id, GUEST_TOKEN).expectNotFound()
    }

    @Test
    fun `try to delete an activity through a user that didn't create the activity gives 401`(){
        val notOwner = testClient.createUser(UserRoutes.UserCreationBody("Joao", "joaosousa@gmail.com"))
        deleteActivity(testSport.id, testActivity.id, notOwner.authToken).expectUnauthorized()
    }

    @Test
    fun `try to delete an activity that does not exist gives 404`(){
        val activityID = "RANDOM_ACTIVITY"

        deleteActivity(testSport.id, activityID, GUEST_TOKEN).expectNotFound()
    }

    @Test
    fun `try to delete an activity with an invalid token gives 401`(){
        deleteActivity(testSport.id, testActivity.id, "invalid_token").expectUnauthorized()
    }

    @Test
    fun `get invalid parameter error trying to get the activities of a sport receiving an invalid date`(){
        getRequest<HttpError>(testClient, "${SPORT_ACTIVITY_PATH}?date=invalido", Response::expectBadRequest)
    }
    @Test
    fun `get an activity that does not exist`(){
        getRequest<HttpError>(
                testClient,
                activityResourceLocation(testSport.id, "invalido"),
                Response::expectNotFound
        )
    }

    @Test
    fun `get an activity that does exist`(){
        getRequest<ActivityDTO>(testClient,activityResourceLocation(testSport.id, testActivity.id), Response::expectOK)
    }

    private fun deleteActivity(sportID: SportID, activityID: ActivityID, token: UserToken)  =
        testClient(
            Request(DELETE, activityResourceLocation(sportID, activityID))
                .header("Authorization", "Bearer $token")
        )
}
