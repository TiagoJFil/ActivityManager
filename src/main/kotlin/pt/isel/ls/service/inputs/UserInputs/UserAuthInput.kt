package pt.isel.ls.service.inputs.UserInputs

import pt.isel.ls.service.UserServices
import pt.isel.ls.service.entities.User.Email
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.hashPassword
import pt.isel.ls.utils.service.requireParameter

class UserAuthInput(authEmail: Param, authPassword: Param) {

    val email: Email
    val password: String

    init {
        email = Email(requireParameter(authEmail, UserServices.EMAIL_PARAM))
        val safePassword = requireParameter(authPassword, UserServices.PASSWORD_PARAM)
        password = hashPassword(safePassword)
    }
}
