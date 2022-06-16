package pt.isel.ls.service.inputs.ActivityInputs

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import pt.isel.ls.service.ActivityServices
import pt.isel.ls.service.ActivityServices.Companion.ROUTE_ID_PARAM
import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.service.SportsServices.Companion.SPORT_ID_PARAM
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.utils.ID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.requireParameter
import java.text.ParseException

class ActivityCreateInput(sid: Param, duration: Param, date: Param, rid: Param) {

    val sportID: ID
    val activityDate: LocalDate
    val routeID: ID?
    val activityDuration: Duration

    init {
        try {

            sportID = validateID(sid, SPORT_ID_PARAM)
            activityDate = checkDate(date)
            routeID = validateNotRequiredID(rid, ROUTE_ID_PARAM)
            activityDuration = checkDuration(duration)
        } catch (e: ParseException) {
            throw InvalidParameter(ActivityServices.DURATION_INVALID_FORMAT)
        } catch (e: IllegalArgumentException) {
            throw InvalidParameter(ActivityServices.DATE_INVALID_FORMAT)
        }
    }

    private fun checkDuration(duration: Param): Duration {
        requireParameter(duration, ActivityServices.DURATION_PARAM)
        return getDurationFromString(duration)
    }

    private fun checkDate(date: Param): LocalDate {
        val safeDate = requireParameter(date, ActivityServices.DATE_PARAM)
        return safeDate.toLocalDate()
    }
}
