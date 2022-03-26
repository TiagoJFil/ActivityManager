package pt.isel.ls.api

import kotlinx.datetime.toLocalDate
import org.http4k.core.Response
import org.junit.Test
import pt.isel.ls.api.ActivityRoutes.ActivityCreation
import pt.isel.ls.api.ActivityRoutes.ActivityIdResponse
import pt.isel.ls.api.SportRoutes.ListActivities
import pt.isel.ls.api.SportRoutes.SportList
import pt.isel.ls.api.utils.*
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.RouteServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.GUEST_TOKEN
import pt.isel.ls.utils.guestUser
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ActivitiesIntegrationTests {
    // create activityServices
    val activityServices = ActivityServices(ActivityDataMemRepository(), UserDataMemRepository(guestUser))
    val userServices = UserServices(UserDataMemRepository(guestUser))
    val sportsServices = SportsServices(SportDataMemRepository())
    val routeServices = RouteServices(RouteDataMemRepository())
    private val backend = getApiRoutes(getAppRoutes(userServices, routeServices, sportsServices,activityServices))

    @Test
    fun `get a user's activities returns empty list`() {
        val activitiesList = getRequest<UserRoutes.ListActivities>(
                backend,
                "$USER_PATH${guestUser.id}/activities",
                Response::expectOK
        ).activities
        assertEquals(listOf(), activitiesList)
    }

    @Test
    fun `get a user's activities`() {

        val sportID = backend.createSport(SportRoutes.SportCreationBody("Futebol")).sportID

        val activityID = backend.createActivity(
                ActivityCreation("05:10:32.123", "2002-12-31", "1234"),
                sportID
        ).activityID

        val activity = Activity(
                activityID,
                "2002-12-31".toLocalDate(),
                "05:10:32.123",
                sportID,
                "1234",
                guestUser.id
        )

        val activitiesList = getRequest<UserRoutes.ListActivities>(
                backend,
                "$USER_PATH${guestUser.id}/activities",
                Response::expectOK
        ).activities

        val expectedList = listOf<Activity>(activity)
        assertEquals(expectedList, activitiesList)
    }

    @Test
    fun `get a list of activities by sport ascending and descending`(){
        val sportID = backend.createSport(SportRoutes.SportCreationBody("Futebol")).sportID

        val activityID1 = backend.createActivity(
                ActivityCreation("05:10:32.123", "2002-12-31", "1234")
                ,sportID
        ).activityID

        val activityID2 = backend.createActivity(
                ActivityCreation("02:10:32.123", "2002-12-30", "1234"),
                sportID
        ).activityID

        val activitiesListDescending= getRequest<ListActivities>(
                backend,
                "$SPORT_PATH${sportID}/activities?orderBy=descending",
                Response::expectOK
        ).activities

        val activitiesListAscending= getRequest<ListActivities>(
                backend,
                "$SPORT_PATH${sportID}/activities?orderBy=ascending",
                Response::expectOK
        ).activities

        val activity1 =
                Activity(activityID1, "2002-12-31".toLocalDate(), "05:10:32.123", sportID, "1234", guestUser.id)

        val activity2 =
                Activity(activityID2, "2002-12-30".toLocalDate(), "02:10:32.123", sportID, "1234", guestUser.id)

        val listExpectedAscending = listOf(activity2, activity1)
        val listExpectedDescending = listOf(activity1, activity2)

        assertContentEquals(listExpectedAscending, activitiesListAscending)
        assertContentEquals(listExpectedDescending, activitiesListDescending)
    }

    @Test
    fun `get a list of activities by sport filtered by date`(){
        val sportID = backend.createSport(SportRoutes.SportCreationBody("Futebol")).sportID

        backend.createActivity(
                ActivityCreation("05:10:32.123", "2002-12-31", "1234")
                ,sportID
        ).activityID

        val activityID2 = backend.createActivity(
                ActivityCreation("02:10:32.123", "2002-05-20", "1234"),
                sportID
        ).activityID

        val activityID3 = backend.createActivity(
                ActivityCreation("03:10:32.123", "2002-05-20", "1234"),
                sportID
        ).activityID

        val activitiesList= getRequest<ListActivities>(
                backend,
                "$SPORT_PATH${sportID}/activities?date=2002-05-20",
                Response::expectOK
        ).activities

        val activity1 =
                Activity(activityID2, "2002-05-20".toLocalDate(), "02:10:32.123", sportID, "1234", guestUser.id)

        val activity2 =
                Activity(activityID3, "2002-05-20".toLocalDate(), "03:10:32.123", sportID, "1234", guestUser.id)

        val expectedActivitiesList = listOf<Activity>(activity1, activity2)

        assertContentEquals(expectedActivitiesList, activitiesList)
    }

    @Test
    fun `get a list of activities by sport filtered by route`(){
        val sportID = backend.createSport(SportRoutes.SportCreationBody("Futebol")).sportID

        backend.createActivity(
                ActivityCreation("05:10:32.123", "2002-12-31", "123456")
                ,sportID
        ).activityID

        val activityID2 = backend.createActivity(
                ActivityCreation("02:10:32.123", "2002-05-20", "1234"),
                sportID
        ).activityID

        val activityID3 = backend.createActivity(
                ActivityCreation("03:10:32.123", "2002-05-20", "1234"),
                sportID
        ).activityID

        val activitiesList= getRequest<ListActivities>(
                backend,
                "$SPORT_PATH${sportID}/activities?rid=1234",
                Response::expectOK
        ).activities

        val activity1 =
                Activity(activityID2, "2002-05-20".toLocalDate(), "02:10:32.123", sportID, "1234", guestUser.id)

        val activity2 =
                Activity(activityID3, "2002-05-20".toLocalDate(), "03:10:32.123", sportID, "1234", guestUser.id)

        val expectedActivitiesList = listOf<Activity>(activity1, activity2)

        assertContentEquals(expectedActivitiesList, activitiesList)
    }


    @Test
    fun `get a list of activities by sport filtered by date and route id`() {
        val sportID = backend.createSport(SportRoutes.SportCreationBody("Futebol")).sportID

        backend.createActivity(
                ActivityCreation("05:10:32.123", "2002-12-31", "123456")
                ,sportID
        ).activityID

        val activityID2 = backend.createActivity(
                ActivityCreation("02:10:32.123", "2002-05-20", "12345"),
                sportID
        ).activityID

        val activitiesList= getRequest<ListActivities>(
                backend,
                "$SPORT_PATH${sportID}/activities?rid=12345&date=2002-05-20",
                Response::expectOK
        ).activities

        val activity1 =
                Activity(activityID2, "2002-05-20".toLocalDate(), "02:10:32.123", sportID, "12345", guestUser.id)

        val expectedActivitiesList = listOf<Activity>(activity1)
        assertContentEquals(expectedActivitiesList, activitiesList)
    }

    @Test
    fun `get a list of activities by sport filtered by inexistent date and route id gives empty list`() {
        val sportID = backend.createSport(SportRoutes.SportCreationBody("Futebol")).sportID

        backend.createActivity(
                ActivityCreation("05:10:32.123", "2002-12-31", "123456")
                ,sportID
        ).activityID

        val activitiesList= getRequest<ListActivities>(
                backend,
                "$SPORT_PATH${sportID}/activities?rid=12345&date=2002-05-20",
                Response::expectOK
        ).activities

        assertContentEquals(listOf(), activitiesList)
    }

    @Test fun `get not found error trying to get the activities of a sport that does not exist`(){
        val id = 123545555555
        getRequest<HttpError>(backend, "${SPORT_PATH}${id}/activities", Response::expectNotFound)
    }

    @Test fun `get invalid parameter error trying to get the activities of a sport receiving an order that doesn't exist`(){
        val sportID = backend.createSport(SportRoutes.SportCreationBody("Futebol")).sportID
        getRequest<HttpError>(backend, "${SPORT_PATH}${sportID}/activities?orderBy=invalido", Response::expectBadRequest)
    }

}
