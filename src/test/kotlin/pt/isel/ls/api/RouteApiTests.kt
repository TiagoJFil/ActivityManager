package pt.isel.ls.api

import org.http4k.core.Response
import org.junit.After
import org.junit.Test
import pt.isel.ls.services.dto.RouteDTO
import pt.isel.ls.api.RouteRoutes.*
import pt.isel.ls.api.utils.*
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testRoute
import pt.isel.ls.services.dto.HttpError
import pt.isel.ls.services.dto.toDTO
import kotlin.test.assertContains
import kotlin.test.assertEquals


class RouteApiTests {

    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV))
    }

    @Test fun `get routes without creating returns a list with the default route`(){
        val routesList = getRequest<RouteList>(testClient, ROUTE_PATH, Response::expectOK)
        assertEquals(listOf(testRoute.toDTO()), routesList.routes)
    }

    @Test fun `create a route successfully`(){
        testClient.createRoute(RouteCreationBody(startLocation = "a", endLocation = "b", distance = 10.0))
    }


    @Test fun `create a route without start location gives 400`(){
        val body = RouteCreationBody(endLocation = "b", distance = 10.0)
        postRequest<RouteCreationBody, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without end location gives 400`(){
        val body = RouteCreationBody(distance = 20.0, startLocation = "a")
        postRequest<RouteCreationBody, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without distance gives 400`(){
        val body = RouteCreationBody(endLocation = "b", startLocation = "c")
        postRequest<RouteCreationBody, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route with a blank parameter gives 400`(){
        val body = RouteCreationBody(startLocation = " ", endLocation = "b", distance = 10.0)
        postRequest<RouteCreationBody, HttpError>(
            testClient,
            ROUTE_PATH,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }


    @Test fun `try to get a route that doesnt exist`(){
        val id = 123

        getRequest<HttpError>(testClient, "${ROUTE_PATH}${id}", Response::expectNotFound)
    }

    @Test fun `get a route successfully`(){
        val body = RouteCreationBody(startLocation = "a", endLocation = "b", distance = 10.0)
        val routeResponse = testClient.createRoute(body)
        getRequest<RouteDTO>(testClient, "$ROUTE_PATH${routeResponse.routeID}", Response::expectOK)
    }

    @Test fun `create multiple routes ensuring they're in the list of routes`(){

        val start = "Lisboa"
        val end = "Fátima"
        val distance = 127.8

        val creationBodies = List(1000){RouteCreationBody("Lisboa", "Fátima", 127.8)}
        val routeIds: List<String> = creationBodies.map { testClient.createRoute(it).routeID }

        val expected = routeIds.map { RouteDTO(id=it, start, end , distance, guestUser.id) }
        val routeList = getRequest<RouteList>(testClient, ROUTE_PATH, Response::expectOK).routes

        expected.forEach { assertContains(routeList, it) }

    }

}