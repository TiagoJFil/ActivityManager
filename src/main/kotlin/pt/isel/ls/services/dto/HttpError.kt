package pt.isel.ls.services.dto

import kotlinx.serialization.Serializable

/**
 * Object sent in body when an error is thrown in the server
 */
@Serializable
data class HttpError(val code: Int, val message: String?)







