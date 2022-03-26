package pt.isel.ls.services

/**
 * Error class used to represent errors in the application.
 * @property message The error message.
 * @property code The error code.
 */
sealed class AppError(val code: Int, message: String): Exception(message)

/**
 * Error thrown when a parameter is invalid.
 * @property parameterName The name of the parameter that is invalid.
 */
data class InvalidParameter(val parameterName: String)
    : AppError(2000, """Invalid parameter: "$parameterName" """)

/**
 * Error thrown when a parameter is missing.
 * @property parameterName The name of the parameter that is missing.
 */
data class MissingParameter(val parameterName: String)
    : AppError(2001, """Missing required parameter: "$parameterName" """)

/**
 * Error thrown when a resource is not found.
 * @property resourceName The name of the resource that is not found.
 * @property resourceID The id of the resource that is not found.
 */
data class ResourceNotFound(val resourceName: String, val resourceID: String)
    : AppError(2002, """ $resourceName with id $resourceID  not found.""")


/**
 * Error thrown when a user is not authenticated.
 * @property message The error message.
 */
data class UnauthenticatedError(override val message: String="Invalid or missing token.")
    : AppError(2003, message)






