package pt.isel.ls.http

import org.http4k.core.Response
import org.junit.Test
import pt.isel.ls.entities.Route
import pt.isel.ls.http.RouteRoutes.*
import pt.isel.ls.http.utils.*
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.*
import pt.isel.ls.utils.*
import kotlin.math.exp
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
        val body = RouteCreation(startLocation = "a", endLocation = "b", distance = 10.0)
        postRequest<RouteCreation, RouteIDResponse>(backend, routePath, body, Response::expectCreated)
    }


    //TODO(Autenthication)
    @Test fun `create a route without start location`(){
        val body = RouteCreation(endLocation = "b", distance = 10.0)
        postRequest<RouteCreation, HttpError>(backend, routePath, body, Response::expectBadRequest)
    }

    @Test fun `create a route without end location`(){
        val body = RouteCreation(distance = 20.0, startLocation = "a")
        postRequest<RouteCreation, HttpError>(backend, routePath, body, Response::expectBadRequest)
    }

    @Test fun `create a route without distance`(){
        val body = RouteCreation(endLocation = "b", startLocation = "c")
        postRequest<RouteCreation, HttpError>(backend, routePath, body, Response::expectBadRequest)
    }

    @Test fun `create a route with a blank parameter`(){
        val body = RouteCreation(startLocation = " ", endLocation = "b", distance = 10.0)
        postRequest<RouteCreation, HttpError>(backend, routePath, body, Response::expectBadRequest)
    }


    @Test fun `try to get a route that doesnt exist`(){
        val id = 123
        println("${routePath}${id}")
        getRequest<HttpError>(backend, "${routePath}${id}", Response::expectNotFound)
    }

    @Test fun `get a route`(){
        val body = RouteCreation(startLocation = "a", endLocation = "b", distance = 10.0)
        val routeResponse = postRequest<RouteCreation, RouteIDResponse>(backend, routePath, body, Response::expectCreated)
        getRequest<Route>(backend, "$routePath${routeResponse.id}", Response::expectOK)
    }

    @Test fun `create multiple routes ensuring they're in the list of routes`(){

        val start = "Lisboa"
        val end = "Fátima"
        val distance = 127.8

        val creationBodies = List(1000){RouteCreation("Lisboa", "Fátima", 127.8)}
        val routeIds: List<String> = creationBodies.map {
            postRequest<RouteCreation, RouteIDResponse>(
                backend,
                routePath,
                it,
                Response::expectCreated
            ).id
        }

        val expected = routeIds.map { Route(id=it, start, end , distance, guestUser.id) }
        val routeList = getRequest<RouteList>(backend, routePath, Response::expectOK).routes

        expected.forEach { assertContains(routeList, it) }

    }




}