package pt.isel.ls.utils

import kotlinx.datetime.toLocalDate
import pt.isel.ls.services.entities.Activity
import pt.isel.ls.services.entities.Route
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.generateRandomId

val guestUser = User(
    name = "guest",
    id = "guestID",
    email = User.Email("guest@gmail.com")
)

val testRoute = Route(
    id = generateRandomId(),
    startLocation = "testStartLocation",
    endLocation = "testEndLocation",
    distance = 23.0,
    user = guestUser.id
)

val testSport = Sport(
    id = generateRandomId(),
    name = "testSport",
    description = "testDescription",
    user = guestUser.id
)

val testActivity = Activity(
    id = generateRandomId(),
    sport = testSport.id,
    route = testRoute.id,
    user = guestUser.id,
    duration = Activity.Duration(500000),
    date = "2002-10-02".toLocalDate()
)

const val GUEST_TOKEN = "TOKEN"