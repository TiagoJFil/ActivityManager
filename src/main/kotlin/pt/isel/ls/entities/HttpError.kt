package pt.isel.ls.entities

import kotlinx.serialization.Serializable

/**
 * Object sent in body when an error is thrown in the server
 */
@Serializable
data class HttpError(val code: Int, val message: String?)







