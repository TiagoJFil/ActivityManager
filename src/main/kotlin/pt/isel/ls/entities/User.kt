package pt.isel.ls.entities

import kotlinx.serialization.Serializable

@Serializable
data class User(val name: String, val email: String, val id: String)

