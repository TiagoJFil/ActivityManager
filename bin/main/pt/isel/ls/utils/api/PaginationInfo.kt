package pt.isel.ls.utils.api

import org.http4k.core.Request
import pt.isel.ls.service.InvalidParameter

/**
 * Class that represents the pagination information of a request.
 * @param limit the maximum number of elements to be returned.
 * @param offset the offset of the first element to be returned.
 */
data class PaginationInfo(val limit: Int, val offset: Int) {
    companion object
}

/**
 * Function that extracts the pagination information from a request.
 * @param request the request to be parsed.
 */
fun PaginationInfo.Companion.fromRequest(request: Request): PaginationInfo { // Made so services and data don't depend on http4k
    val limit = request.query("limit")?.toIntOrNull() ?: 10
    val offset = request.query("skip")?.toIntOrNull() ?: 0
    if (limit <= 0) throw InvalidParameter("limit must be positive")
    if (offset < 0) throw InvalidParameter("offset must be non-negative")
    return PaginationInfo(limit, offset)
}
