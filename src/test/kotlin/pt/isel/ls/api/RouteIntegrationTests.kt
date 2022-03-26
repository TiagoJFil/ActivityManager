package pt.isel.ls.api

import org.http4k.core.Response
import org.junit.Test
import pt.isel.ls.entities.Route
import pt.isel.ls.api.RouteRoutes.*
import pt.isel.ls.api.utils.*
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.*
import pt.isel.ls.utils.*
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RouteIntegrationTests {
    private val routePath = "/api/routes/"
    private val routeServices = RouteServices(RouteDataMemRepository())
    private val userServices = UserServices(UserDataMemRepository(guestUser))
    private val backend = getApiRoutes(Route(routeServices, userServices))

    @Test fun `get routes without creating returns empty list`(){
        val routesList = getRequest<RouteList>(backend, routePath, Response::expectOK)
        assertEquals(emptyList(), routesList.routes)
    }

    @Test fun `create a route successfully`(){
        createRoute(RouteCreation(startLocation = "a", endLocation = "b", distance = 10.0))
    }


    @Test fun `create a route without start location gives 400`(){
        val body = RouteCreation(endLocation = "b", distance = 10.0)
        postRequest<RouteCreation, HttpError>(
            backend,
            routePath,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without end location gives 400`(){
        val body = RouteCreation(distance = 20.0, startLocation = "a")
        postRequest<RouteCreation, HttpError>(
            backend,
            routePath,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route without distance gives 400`(){
        val body = RouteCreation(endLocation = "b", startLocation = "c")
        postRequest<RouteCreation, HttpError>(
            backend,
            routePath,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a route with a blank parameter gives 400`(){
        val body = RouteCreation(startLocation = " ", endLocation = "b", distance = 10.0)
        postRequest<RouteCreation, HttpError>(
            backend,
            routePath,
            body,
            headers = authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }


    @Test fun `try to get a route that doesnt exist`(){
        val id = 123
        println("${routePath}${id}")
        getRequest<HttpError>(backend, "${routePath}${id}", Response::expectNotFound)
    }

    @Test fun `get a route successfully`(){
        val body = RouteCreation(startLocation = "a", endLocation = "b", distance = 10.0)
        val routeResponse = createRoute(body)
        getRequest<Route>(backend, "$routePath${routeResponse.routeID}", Response::expectOK)
    }

    @Test fun `create multiple routes ensuring they're in the list of routes`(){

        val start = "Lisboa"
        val end = "Fátima"
        val distance = 127.8

        val creationBodies = List(1000){RouteCreation("Lisboa", "Fátima", 127.8)}
        val routeIds: List<String> = creationBodies.map { createRoute(it).routeID }

        val expected = routeIds.map { Route(id=it, start, end , distance, guestUser.id) }
        val routeList = getRequest<RouteList>(backend, routePath, Response::expectOK).routes

        expected.forEach { assertContains(routeList, it) }

    }

    /**
     * Helper function to create a route, ensures it is created and returns the respective [RouteIDResponse]
     * @param routeCreation the route creation body. Must be valid.
     */
    private fun createRoute(routeCreation: RouteCreation): RouteIDResponse
        = postRequest<RouteCreation, RouteIDResponse>(
            backend,
            routePath,
            routeCreation,
            authHeader(GUEST_TOKEN),
            Response::expectCreated
        )





}