package pt.isel.ls.utils.repository

/**
 * Generates an id based on the current time in milliseconds and a random number.
 */
fun generateRandomId(): String = (System.currentTimeMillis() * Math.random() * 10).toLong().toString()