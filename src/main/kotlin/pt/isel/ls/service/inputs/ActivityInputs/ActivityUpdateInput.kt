package pt.isel.ls.service.inputs.ActivityInputs

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import pt.isel.ls.service.ActivityServices
import pt.isel.ls.service.ActivityServices.Companion.ACTIVITY_ID_PARAM
import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.service.SportsServices.Companion.SPORT_ID_PARAM
import pt.isel.ls.service.entities.Activity.Duration
import pt.isel.ls.utils.ID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.requireIdInteger
import java.text.ParseException

class ActivityUpdateInput(sid: Param, aid: Param, duration: Param, date: Param, rid: Param) {

    val sportID: ID?
    val activityID: ID
    val routeID: ID?
    val activityDuration: Duration?
    val activityDate: LocalDate?
    val removeRoute: Boolean
    val hasNothingToUpdate: Boolean

    init {
        try {

            sportID = validateNotRequiredID(sid, SPORT_ID_PARAM)
            activityID = validateID(aid, ACTIVITY_ID_PARAM)
            activityDuration = checkDuration(duration)
            routeID = handleRoute(rid)
            removeRoute = rid?.isBlank() == true
            activityDate = date?.toLocalDate()
            hasNothingToUpdate = duration == null && date == null && rid == null
        } catch (e: ParseException) {
            throw InvalidParameter(ActivityServices.DURATION_INVALID_FORMAT)
        } catch (e: IllegalArgumentException) {
            throw InvalidParameter(ActivityServices.DATE_INVALID_FORMAT)
        }
    }

    private fun checkDuration(duration: Param): Duration? {
        val handledDuration = duration?.ifBlank { null }
        return handledDuration?.let { getDurationFromString(it) }
    }

    private fun handleRoute(rid: Param): Int? {
        val handledRoute = rid?.ifBlank { null }
        return handledRoute?.let { requireIdInteger(it, ActivityServices.ROUTE_ID_PARAM) }
    }
}
