package pt.isel.ls.service.inputs.RouteInputs

import pt.isel.ls.service.ActivityServices.Companion.ROUTE_ID_PARAM
import pt.isel.ls.service.RouteServices.Companion.DISTANCE_PARAM
import pt.isel.ls.service.RouteServices.Companion.END_LOCATION_PARAM
import pt.isel.ls.service.RouteServices.Companion.START_LOCATION_PARAM
import pt.isel.ls.service.inputs.ActivityInputs.validateID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.requireNotBlankParameter
import pt.isel.ls.utils.service.requireValidDistance

class RouteUpdateInput(rid: Param, sLocation: Param, eLocation: Param, routeDistance: Float?) {

    val routeID: Int
    val startLocation: Param
    val endLocation: Param
    val distance: Float?
    val hasNothingToUpdate: Boolean

    init {
        routeID = validateID(rid, ROUTE_ID_PARAM)
        startLocation = requireNotBlankParameter(sLocation, START_LOCATION_PARAM)
        endLocation = requireNotBlankParameter(eLocation, END_LOCATION_PARAM)
        requireValidDistance(routeDistance, DISTANCE_PARAM)
        distance = routeDistance
        hasNothingToUpdate = startLocation == null && endLocation == null && distance == null
    }
}
