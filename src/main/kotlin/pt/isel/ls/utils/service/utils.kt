package pt.isel.ls.utils.service

import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.service.MissingParameter
import pt.isel.ls.service.ResourceNotFound
import pt.isel.ls.service.UnauthenticatedError
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import java.util.UUID

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @throws [InvalidParameter] if the parameter is blank.
 */
fun requireNotBlankParameter(parameter: String?, parameterName: String) {
    if (parameter != null && parameter.isBlank()) throw InvalidParameter(parameterName)
}

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @throws [MissingParameter] if the parameter is null.
 * @throws [InvalidParameter] if the parameter is blank.
 */
fun requireParameter(parameter: String?, parameterName: String): String {
    if (parameter == null) throw MissingParameter(parameterName)
    requireNotBlankParameter(parameter, parameterName)
    return parameter
}

fun requireIdInteger(id: String, parameterName: String): Int =
    id.toIntOrNull()
        ?: throw InvalidParameter("$parameterName must be an integer")

/**
 * Ensures that there is a token associated with the user.
 *
 * @param token the user token to check.
 * @return [UserID] the user ID associated with the token.
 * @throws [UnauthenticatedError] if the token is not valid.
 */
fun UserRepository.requireAuthenticated(token: UserToken?): UserID {
    if (token == null) throw UnauthenticatedError()
    return getUserIDBy(token) ?: throw UnauthenticatedError()
}

/**
 * Ensures that the sport identified by the given id exists.
 *
 * @param sportID the sport ID to check.
 * @throws ResourceNotFound if the sport does not exist.
 */
fun SportRepository.requireSport(sportID: SportID) {
    if (!hasSport(sportID)) throw ResourceNotFound("Sport", sportID.toString())
}

/**
 * Ensures that the user identified by the given id exists.
 *
 * @param userID the user ID to check.
 * @throws ResourceNotFound if the user does not exist.
 */
fun UserRepository.requireUser(userID: UserID) {
    if (!hasUser(userID)) throw ResourceNotFound("User", userID.toString())
}

/**
 * Ensures that the activity identified by the given id exists.
 *
 * @param activityID the activity ID to check.
 * @throws ResourceNotFound if the user does not exist.
 */
fun ActivityRepository.requireActivity(activityID: ActivityID) {
    if (!hasActivity(activityID)) throw ResourceNotFound("Activity", activityID.toString())
}

fun ActivityRepository.requireActivityWith(activityID: ActivityID, sportID: SportID) {
    val activity = getActivity(activityID) ?: throw ResourceNotFound("Activity", activityID.toString())
    if (activity.sport != sportID) throw InvalidParameter("\"sid\" not associated with given \"aid\"")
}

/**
 * Ensures that the route identified by the given id exists.
 *
 * @param routeID the [RouteID] to check.
 * @throws ResourceNotFound if the route does not exist.
 */
fun RouteRepository.requireRoute(routeID: RouteID) {
    if (!hasRoute(routeID)) throw ResourceNotFound("Route", routeID.toString())
}

/**
 * Generates a random UUID.
 */
fun generateUUId() = UUID.randomUUID().toString()

/**
 *
 */
fun <T> List<T>.applyPagination(paginationInfo: PaginationInfo) = drop(paginationInfo.offset).take(paginationInfo.limit)
