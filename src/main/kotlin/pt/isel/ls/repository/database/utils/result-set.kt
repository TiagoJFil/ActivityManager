package pt.isel.ls.repository.database.utils

import java.sql.ResultSet

/**
 * Takes the result of [function] and returns it if the result set has a next row otherwise returns null.
 */
fun <T> ResultSet.ifNext(function: () -> T): T? = if (next()) function() else null
