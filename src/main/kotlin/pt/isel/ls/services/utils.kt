package pt.isel.ls.services

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @throws [InvalidParameter] if the parameter is blank.
 */
fun requireNotBlankParameter(parameter: String?, parameterName: String) {
    if(parameter != null && parameter.isBlank()) throw InvalidParameter(parameterName)
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