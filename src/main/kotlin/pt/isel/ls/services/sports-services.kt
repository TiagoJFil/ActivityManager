package pt.isel.ls.services

import java.lang.Math.random
import java.util.UUID

/**
 * Generates a random UUID.
 */
fun generateUUId() = UUID.randomUUID().toString()

/**
 * Generates an id based on the current time in milliseconds and a random number.
 */
fun generateRandomId(): String = (System.currentTimeMillis() * random() * 10).toLong().toString()
