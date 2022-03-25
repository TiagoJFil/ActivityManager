package pt.isel.ls.services


sealed class AppError(val code: Int, message: String): Exception(message)

data class InvalidParameter(val parameterName: String)
    : AppError(2000, """Invalid parameter: "$parameterName" """)

data class MissingParameter(val parameterName: String)
    : AppError(2001, """Missing required parameter: "$parameterName" """)

data class ResourceNotFound(val resourceName: String, val resourceID: String)
    : AppError(2002, """ $resourceName with id $resourceID  not found.""")






