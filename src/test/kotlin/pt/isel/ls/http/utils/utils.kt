package pt.isel.ls.http.utils


/**
 * Auth header
 */
fun authHeader(token: String) = listOf("Authorization" to  "Bearer $token")