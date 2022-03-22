package pt.isel.ls.utils

import pt.isel.ls.entities.Email
import pt.isel.ls.entities.User

val guestUser = User(
    name="guest",
    id="guestID",
    email= Email("guest@gmail.com")
)