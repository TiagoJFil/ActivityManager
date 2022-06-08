package pt.isel.ls.utils.service

import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.repository.RouteRepository
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.AuthorizationError
import pt.isel.ls.service.InvalidParameter
import pt.isel.ls.service.MissingParameter
import pt.isel.ls.service.ResourceNotFound
import pt.isel.ls.service.RouteServices
import pt.isel.ls.service.SportsServices
import pt.isel.ls.service.UnauthenticatedError
import pt.isel.ls.service.UserServices
import pt.isel.ls.service.entities.Route
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.service.entities.User
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import java.security.MessageDigest
import java.util.UUID

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @return the parameter if it is not null, otherwise throw an exception.
 * @throws [InvalidParameter] if the parameter is blank.
 */
fun requireNotBlankParameter(parameter: String?, parameterName: String): String? {
    if (parameter != null && parameter.isBlank()) throw InvalidParameter(parameterName)
    if (parameter != null) requireLengths(parameter, parameterName)
    return parameter
}

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @return the parameter if it is not null, otherwise throw an exception.
 * @throws [MissingParameter] if the parameter is null or is lower than 0.
 */
fun requireValidDistance(parameter: Float?, parameterName: String): Float? {
    if (parameter != null && parameter <= 0) throw InvalidParameter(parameterName)
    if (parameter != null && parameter > 50_000) throw InvalidParameter("parameterName")
    return parameter
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

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * Requires the parameter to be less than its respective MAX_LENGTH.
 */
private fun requireLengths(parameter: String, parameterName: String) {
    when (parameterName) {
        SportsServices.NAME_PARAM -> {
            if (parameter.length > Sport.MAX_NAME_LENGTH)
                throw InvalidParameter("${SportsServices.NAME_PARAM} is too long, max length is ${Sport.MAX_NAME_LENGTH}")
        }
        UserServices.NAME_PARAM -> {
            if (parameter.length > User.MAX_NAME_LENGTH)
                throw InvalidParameter("${UserServices.NAME_PARAM} is too long, max length is ${User.MAX_NAME_LENGTH}")
        }
        RouteServices.END_LOCATION_PARAM -> {
            if (parameter.length > Route.MAX_LOCATION_LENGTH)
                throw InvalidParameter("${RouteServices.END_LOCATION_PARAM} is too long, max length is ${Route.MAX_LOCATION_LENGTH}")
        }
        RouteServices.START_LOCATION_PARAM -> {
            if (parameter.length > Route.MAX_LOCATION_LENGTH)
                throw InvalidParameter("${RouteServices.START_LOCATION_PARAM} is too long, max length is ${Route.MAX_LOCATION_LENGTH}")
        }
        else -> {}
    }
}

/**
 * @param id the id to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @return the id if it is not null, otherwise throw an exception.
 */
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
 * Ensures that the user owns the received sport.
 */
fun SportRepository.requireOwnership(userID: UserID, sportID: SportID) {
    val route = getSport(sportID) ?: throw ResourceNotFound("Sport", sportID.toString())
    if (route.user != userID) throw AuthorizationError("You are not the owner of this sport")
}
/**
 * Ensures that the user owns the received route.
 */
fun RouteRepository.requireOwnership(userID: UserID, routeID: RouteID) {
    val route = getRoute(routeID) ?: throw ResourceNotFound("Route", routeID.toString())
    if (userID != route.user) throw AuthorizationError("You are not the owner of this route")
}
/**
 * Ensures that the user owns the received activity.
 */
fun ActivityRepository.requireOwnership(userID: UserID, activityID: ActivityID) {
    val activity = getActivity(activityID) ?: throw ResourceNotFound("Activity", activityID.toString())
    if (userID != activity.user) throw AuthorizationError("You are not the owner of this activity")
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
 * Returns the given list with the elements specified by [PaginationInfo].
 */
fun <T> List<T>.applyPagination(paginationInfo: PaginationInfo) = drop(paginationInfo.offset).take(paginationInfo.limit)

private val digest = MessageDigest.getInstance("SHA-512") // "SHA-512"

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun hashPassword(password: String): String =
    digest.digest(password.toByteArray()).toHex()
