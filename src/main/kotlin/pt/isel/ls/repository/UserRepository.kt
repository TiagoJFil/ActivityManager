package pt.isel.ls.repository

import pt.isel.ls.entities.User

interface UserRepository{

    fun getUserByID(id: String): User?

}
