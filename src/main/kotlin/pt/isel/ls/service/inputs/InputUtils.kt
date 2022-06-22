package pt.isel.ls.service.inputs.ActivityInputs

import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.utils.ID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireNotBlankParameter
import pt.isel.ls.utils.service.requireParameter
import java.util.Date

fun validateID(id: Param, parameter: String): ID {
    val stringID = requireParameter(id, parameter)
    return requireIdInteger(stringID, parameter)
}

fun validateNotRequiredID(id: Param, parameter: String): ID? {
    val stringID = requireNotBlankParameter(id, parameter)
    return stringID?.let {
        requireIdInteger(stringID, parameter)
    }
}

fun getDurationFromString(duration: Param): Duration {
    val parsedDuration: Date = Duration.format.parse(duration)
    val millis: Long = parsedDuration.time
    return Duration(millis)
}
