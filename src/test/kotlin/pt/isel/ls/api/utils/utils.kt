package pt.isel.ls.api.utils


/**
 * Auth header
 */
fun authHeader(token: String) = listOf("Authorization" to  "Bearer $token")