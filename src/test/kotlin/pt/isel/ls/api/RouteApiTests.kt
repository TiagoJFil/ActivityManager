package pt.isel.ls.api

import org.http4k.core.Response
import org.junit.After
import org.junit.Test
import pt.isel.ls.api.RouteRoutes.RouteCreationInput
import pt.isel.ls.api.RouteRoutes.RouteListOutput
import pt.isel.ls.api.utils.ROUTE_PATH
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.api.utils.authHeader
import pt.isel.ls.api.utils.createRoute
import pt.isel.ls.api.utils.expectBadRequest
import pt.isel.ls.api.utils.expectNotFound
import pt.isel.ls.api.utils.expectOK
import pt.isel.ls.api.utils.getRequest
import pt.isel.ls.api.utils.postRequest
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testRoute
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.service.dto.RouteDTO
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RouteApiTests {

    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV))
    }

    @Test fun `get routes without creating returns a list with the default route`() {
        val routesList = getRequest<RouteListOutput>(testClient, ROUTE_PATH, Response::expectOK)
        assertEquals(listOf(testRoute.toDTO()), routesList.routes)
    }

    @Test fun `create a route successfully`() {
        testClient.createRoute(RouteCreationInput(startLocation = "a", endLocation = "b", distance = 10.0))
    }

    @Test fun `create a route without start location gives 400`() {
        val body = RouteCreationInput(endLocation = "b", distance = 10.0)
        postRequest<RouteCreationInput, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without end location gives 400`() {
        val body = RouteCreationInput(distance = 20.0, startLocation = "a")
        postRequest<RouteCreationInput, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without distance gives 400`() {
        val body = RouteCreationInput(endLocation = "b", startLocation = "c")
        postRequest<RouteCreationInput, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route with a blank parameter gives 400`() {
        val body = RouteCreationInput(startLocation = " ", endLocation = "b", distance = 10.0)
        postRequest<RouteCreationInput, HttpError>(
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
        val body = RouteCreationInput(startLocation = "a", endLocation = "b", distance = 10.0)
        val routeResponse = testClient.createRoute(body)
        getRequest<RouteDTO>(testClient, "$ROUTE_PATH${routeResponse.routeID}", Response::expectOK)
    }

    @Test fun `create multiple routes ensuring they're in the list of routes`() {

        val start = "Lisboa"
        val end = "Fátima"
        val distance = 127.8

        val creationBodies = List(1000) { RouteCreationInput("Lisboa", "Fátima", 127.8) }
        val routeIds: List<RouteID> = creationBodies.map { testClient.createRoute(it).routeID }

        val expected = routeIds.map { RouteDTO(id = it, start, end, distance, guestUser.id) }
        val routeList = getRequest<RouteListOutput>(testClient, ROUTE_PATH, Response::expectOK).routes

        expected.forEach { assertContains(routeList, it) }
    }
}
