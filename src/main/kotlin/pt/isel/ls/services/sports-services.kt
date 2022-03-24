package pt.isel.ls.services

import java.lang.Math.random
import java.util.UUID

fun generateUUId() = UUID.randomUUID().toString()

// Less costier than generating a UUid
fun generateRandomId(): String = (System.currentTimeMillis() * random() * 10).toLong().toString()
