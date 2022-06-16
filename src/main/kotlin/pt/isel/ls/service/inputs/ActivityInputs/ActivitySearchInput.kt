package pt.isel.ls.service.inputs.ActivityInputs

import kotlinx.datetime.LocalDate
import pt.isel.ls.service.ActivityServices
import pt.isel.ls.service.ActivityServices.Companion.DATE_PARAM
import pt.isel.ls.service.ActivityServices.Companion.ROUTE_ID_PARAM
import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.service.SportsServices.Companion.SPORT_ID_PARAM
import pt.isel.ls.service.entities.Order
import pt.isel.ls.utils.ID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.requireNotBlankParameter

class ActivitySearchInput(sid: Param, orderBy: Param, date: Param, rid: Param) {

    val sportID: ID
    val order: Order
    val activityDate: LocalDate?
    val routeID: ID?

    init {
        try {

            sportID = validateID(sid, SPORT_ID_PARAM)
            order = get(orderBy)
            activityDate = checkDateIfNotNull(date)
            routeID = validateNotRequiredID(rid, ROUTE_ID_PARAM)
        } catch (e: IllegalArgumentException) {
            throw InvalidParameter(DATE_PARAM)
        }
    }

    private fun get(orderBy: Param) = when (orderBy?.lowercase()) {
        "ascending", null -> Order.ASCENDING
        "descending" -> Order.DESCENDING
        else -> throw InvalidParameter(ActivityServices.ORDER_POSSIBLE_VALUES)
    }

    private fun checkDateIfNotNull(date: Param): LocalDate? {
        requireNotBlankParameter(date, DATE_PARAM)
        return if (date != null) LocalDate.parse(date) else null
    }
}
