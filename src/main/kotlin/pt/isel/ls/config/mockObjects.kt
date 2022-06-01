package pt.isel.ls.config

import kotlinx.datetime.toLocalDate
import pt.isel.ls.service.entities.Activity
import pt.isel.ls.service.entities.Route
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.service.entities.User
import pt.isel.ls.utils.service.hashPassword

val guestUser = User(
    name = "guest",
    id = 0,
    email = User.Email("guest@gmail.com"),
    passwordToken = hashPassword("guest")
)

val testRoute = Route(
    id = 0,
    startLocation = "testStartLocation",
    endLocation = "testEndLocation",
    distance = 23.0F,
    user = guestUser.id
)

val testSport = Sport(
    id = 0,
    name = "testSport",
    description = "testDescription",
    user = guestUser.id
)

val testActivity = Activity(
    id = 0,
    sport = testSport.id,
    route = testRoute.id,
    user = guestUser.id,
    duration = Activity.Duration(500000),
    date = "2002-10-02".toLocalDate()
)

const val GUEST_TOKEN = "TOKEN"

const val GUEST_PASSWORD = hashPassword("PASSWORD")
