package pt.isel.ls.utils.api

import org.http4k.core.Request
import org.http4k.core.Response
import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.utils.UserToken

/**
 * Gets the user token from the request if it exists
 * otherwise returns null
 */
val Request.bearerToken: UserToken?
    get() =
        header("Authorization")
            ?.substringAfter("Bearer ", missingDelimiterValue = "")
            ?.ifBlank { null }

/**
 * Extracts the pagination information from a request.
 */
val Request.pagination: PaginationInfo
    get() {
        val limit = query("limit")?.toIntOrNull() ?: 10
        val offset = query("skip")?.toIntOrNull() ?: 0
        if (limit <= 0) throw InvalidParameter("limit must be positive")
        if (offset < 0) throw InvalidParameter("offset must be non-negative")
        return PaginationInfo(limit, offset)
    }

/**
 * Sets the content type of the response to application/json
 */
fun Response.json(json: String): Response = header("content-Type", "application/json").body(json)
