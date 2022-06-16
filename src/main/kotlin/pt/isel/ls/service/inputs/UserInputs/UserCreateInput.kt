package pt.isel.ls.service.inputs.UserInputs

import pt.isel.ls.service.UserServices
import pt.isel.ls.service.entities.User.Email
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.hashPassword
import pt.isel.ls.utils.service.requireParameter

class UserCreateInput(userName: Param, userEmail: Param, userPassword: Param) {

    val name: String
    val email: Email
    val password: String

    init {
        name = requireParameter(userName, UserServices.NAME_PARAM)
        val safeEmail = requireParameter(userEmail, UserServices.EMAIL_PARAM)
        email = Email(safeEmail)
        val safePassword = requireParameter(userPassword, UserServices.PASSWORD_PARAM)
        password = hashPassword(safePassword)
    }
}
