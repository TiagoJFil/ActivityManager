package pt.isel.ls.service.inputs.RouteInputs

import pt.isel.ls.service.MissingParameter
import pt.isel.ls.service.RouteServices
import pt.isel.ls.service.RouteServices.Companion.DISTANCE_PARAM
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.requireValidDistance

class RouteCreateInput(sLocation: String?, eLocation: String?, routeDistance: Float?) {

    val startLocation: String
    val endLocation: String
    val distance: Float

    init {
        startLocation = requireParameter(sLocation, RouteServices.START_LOCATION_PARAM)
        endLocation = requireParameter(eLocation, RouteServices.START_LOCATION_PARAM)
        if (routeDistance == null) throw MissingParameter(DISTANCE_PARAM)
        requireValidDistance(routeDistance, DISTANCE_PARAM)
        distance = routeDistance
    }
}
