package pt.isel.ls.api

import org.http4k.core.Response
import org.junit.After
import org.junit.Test
import pt.isel.ls.api.RouteRoutes.RouteInput
import pt.isel.ls.api.RouteRoutes.RouteListOutput
import pt.isel.ls.api.UserRoutes.UserInput
import pt.isel.ls.api.utils.ROUTE_PATH
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.api.utils.authHeader
import pt.isel.ls.api.utils.createRoute
import pt.isel.ls.api.utils.createUser
import pt.isel.ls.api.utils.expectBadRequest
import pt.isel.ls.api.utils.expectForbidden
import pt.isel.ls.api.utils.expectNotFound
import pt.isel.ls.api.utils.expectOK
import pt.isel.ls.api.utils.expectUnauthorized
import pt.isel.ls.api.utils.getRequest
import pt.isel.ls.api.utils.postRequest
import pt.isel.ls.api.utils.putRequest
import pt.isel.ls.api.utils.updateResource
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testRoute
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.service.dto.RouteDTO
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.repository.transactions.InMemoryTransactionScope
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RouteApiTests {

    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        InMemoryTransactionScope.reset()
    }

    @Test fun `get routes without creating returns a list with the default route`() {
        val routesList = getRequest<RouteListOutput>(testClient, ROUTE_PATH, Response::expectOK)
        assertEquals(listOf(testRoute.toDTO()), routesList.routes)
    }

    @Test fun `create a route successfully`() {
        testClient.createRoute(RouteInput(startLocation = "a", endLocation = "b", distance = 10.0F))
    }

    @Test fun `create a route without start location gives 400`() {
        val body = RouteInput(endLocation = "b", distance = 10.0F)
        postRequest<RouteInput, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without end location gives 400`() {
        val body = RouteInput(distance = 20.0F, startLocation = "a")
        postRequest<RouteInput, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without distance gives 400`() {
        val body = RouteInput(endLocation = "b", startLocation = "c")
        postRequest<RouteInput, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route with a blank parameter gives 400`() {
        val body = RouteInput(startLocation = " ", endLocation = "b", distance = 10.0F)
        postRequest<RouteInput, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `try to get a route that doesnt exist`() {
        val id = 123

        getRequest<HttpError>(testClient, "${ROUTE_PATH}$id", Response::expectNotFound)
    }

    @Test fun `get a route successfully`() {
        val body = RouteInput(startLocation = "a", endLocation = "b", distance = 10.0F)
        val routeResponse = testClient.createRoute(body)
        getRequest<RouteDTO>(testClient, "$ROUTE_PATH${routeResponse.routeID}", Response::expectOK)
    }

    @Test fun `create multiple routes ensuring they're in the list of routes`() {

        val start = "Lisboa"
        val end = "Fátima"
        val distance = 127.8F

        val creationBodies = List(1000) { RouteInput("Lisboa", "Fátima", 127.8F) }
        val routeIds: List<RouteID> = creationBodies.map { testClient.createRoute(it).routeID }

        val expected = routeIds.map { RouteDTO(id = it, start, end, distance, guestUser.id) }
        val routeList = getRequest<RouteListOutput>(testClient, "$ROUTE_PATH?limit=1005", Response::expectOK).routes

        expected.forEach { assertContains(routeList, it) }
    }

    @Test fun `update an existing route`() {
        val body = RouteInput(startLocation = "a", endLocation = "b", distance = 10.0F)
        val routeResponse = testClient.createRoute(body)
        val routeId = routeResponse.routeID
        val updatedBody = RouteInput(startLocation = "c", endLocation = "d", distance = 20.0F)

        testClient.updateResource<RouteInput, RouteID>(updatedBody, routeId, GUEST_TOKEN)
        val updatedRoute = getRequest<RouteDTO>(testClient, "$ROUTE_PATH$routeId", Response::expectOK)

        assertEquals(updatedRoute.startLocation, updatedBody.startLocation)
        assertEquals(updatedRoute.endLocation, updatedBody.endLocation)
        assertEquals(updatedRoute.distance, updatedBody.distance)
    }

    @Test fun `update a route with a blank parameter gives 400`() {
        val body = RouteInput(startLocation = "", endLocation = "b", distance = 10.0F)
        //  testClient.updateResource<RouteInput,RouteID>(body, testRoute.id)
        putRequest<RouteInput>(
            testClient,
            "$ROUTE_PATH${testRoute.id}",
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )

        val updatedRoute = getRequest<RouteDTO>(testClient, "$ROUTE_PATH${testRoute.id}", Response::expectOK)
        assertEquals(updatedRoute.startLocation, testRoute.startLocation)
        assertEquals(updatedRoute.endLocation, testRoute.endLocation)
        assertEquals(updatedRoute.distance, testRoute.distance)
    }

    @Test fun `update a route that doesnt exist gives 404`() {
        val body = RouteInput(startLocation = "a", endLocation = "b", distance = 10.0F)
        putRequest<RouteInput>(
            testClient,
            "${ROUTE_PATH}231",
            body,
            authHeader(GUEST_TOKEN),
            Response::expectNotFound
        )
    }

    @Test fun `update a route with an not known token gives 401`() {
        val body = RouteInput(startLocation = "a", endLocation = "b", distance = 10.0F)
        putRequest<RouteInput>(
            testClient,
            "$ROUTE_PATH${testRoute.id}",
            body,
            authHeader("not a token"),
            Response::expectUnauthorized
        )
    }

    @Test fun `update a route with a user that is not the owner gives 403`() {
        val user = testClient.createUser(UserInput("test", "test@gmail.com", "password"))

        val body = RouteInput(startLocation = "a", endLocation = "b", distance = 10.0F)
        putRequest<RouteInput>(
            testClient,
            "${ROUTE_PATH}${testRoute.id}",
            body,
            authHeader(user.authToken),
            Response::expectForbidden
        )
    }

    @Test fun `update route without startLocation keeps the old one`() {
        val body = RouteInput(endLocation = "b", distance = 10.0F)

        testClient.updateResource(body, testRoute.id, GUEST_TOKEN)

        val updatedRoute = getRequest<RouteDTO>(testClient, "$ROUTE_PATH${testRoute.id}", Response::expectOK)
        assertEquals(updatedRoute.startLocation, testRoute.startLocation)
    }
}
