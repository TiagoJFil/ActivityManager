package pt.isel.ls.utils.api

import org.http4k.core.Request
import pt.isel.ls.service.InvalidParameter

data class PaginationInfo(val limit: Int, val offset: Int) {
    companion object {
        fun fromRequest(request: Request): PaginationInfo {
            val limit = request.query("limit")?.toIntOrNull() ?: 10
            val offset = request.query("skip")?.toIntOrNull() ?: 0
            if (limit <= 0) throw InvalidParameter("limit must be positive")
            if (offset < 0) throw InvalidParameter("offset must be non-negative")
            return PaginationInfo(limit, offset)
        }
    }
}
