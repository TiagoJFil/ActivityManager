package pt.isel.ls.utils.api

import org.http4k.core.Request
import org.http4k.core.Response
import pt.isel.ls.utils.UserToken

/**
 * Gets the user token from the request
 *
 * @param request request to get the token from
 * @return the user token or null if not found or in invalid format
 */
fun getBearerToken(request: Request): UserToken? =
    request
        .header("Authorization")
        ?.substringAfter("Bearer ", missingDelimiterValue = "")
        ?.ifBlank { null }

/**
 * Sets the content type of the response to application/json
 */
fun Response.contentJson(): Response = header("content-Type", "application/json")
