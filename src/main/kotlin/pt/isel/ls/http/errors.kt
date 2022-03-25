package pt.isel.ls.http

import kotlinx.serialization.Serializable

@Serializable
data class HttpError(val code: Int, val message: String?)







