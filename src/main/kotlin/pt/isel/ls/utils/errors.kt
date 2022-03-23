package pt.isel.ls.utils

import org.http4k.core.Status
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class BadRequestException(message: String): Exception(message)
class NotFoundException(message: String): Exception(message)
class UnauthorizedException(message: String): Exception(message)


fun throwHttpErrorIf(condition : Boolean, error : Status, lazyMessage:  () -> Any){

    if(condition) {
        val message = lazyMessage().toString()
        when (error) {
            Status.BAD_REQUEST -> throw BadRequestException(message)
            Status.NOT_FOUND -> throw   NotFoundException(message)
            Status.UNAUTHORIZED -> throw UnauthorizedException(message)
        }
    }
}

fun String?.isNull(): Boolean = this == null
fun String?.isNotNull(): Boolean = !this.isNull()