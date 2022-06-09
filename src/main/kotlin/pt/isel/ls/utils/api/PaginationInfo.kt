package pt.isel.ls.utils.api

/**
 * Class that represents the pagination information of a request.
 * @param limit the maximum number of elements to be returned.
 * @param offset the offset of the first element to be returned.
 */
data class PaginationInfo(val limit: Int, val offset: Int)
