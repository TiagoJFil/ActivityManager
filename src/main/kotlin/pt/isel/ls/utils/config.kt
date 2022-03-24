package pt.isel.ls.utils

import pt.isel.ls.entities.User

val guestUser = User(
    name = "guest",
    id = "guestID",
    email = User.Email("guest@gmail.com")
)
