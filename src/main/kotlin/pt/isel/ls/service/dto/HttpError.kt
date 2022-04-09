package pt.isel.ls.service.dto

import kotlinx.serialization.Serializable

/**
 * Object sent in body when an error is thrown in the server
 */
@Serializable
data class HttpError(val code: Int, val message: String?)
