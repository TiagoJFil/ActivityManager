package pt.isel.ls.services

import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.repository.memory.UserID
import pt.isel.ls.repository.memory.UserToken
import java.lang.Math.random
import java.util.*


fun generateUUId() = UUID.randomUUID().toString()

//Less costier than generating a UUid
fun generateRandomId() : String = (System.currentTimeMillis() * random() * 10).toLong().toString()

