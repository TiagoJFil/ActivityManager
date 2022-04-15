package pt.isel.ls.utils.api

import org.http4k.core.Request

data class PaginationInfo(val limit: Int, val offset: Int) {
    companion object {
        fun fromRequest(request: Request): PaginationInfo {
            val limit = request.query("limit")?.toIntOrNull() ?: 10
            val offset = request.query("offset")?.toIntOrNull() ?: 0
            return PaginationInfo(limit, offset)
        }
    }
}
