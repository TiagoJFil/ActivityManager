package pt.isel.ls.services


fun requireParameter(parameter: String?, parameterName: String): String {
    if (parameter == null) throw MissingParameter(parameterName)
    if(parameter.isBlank()) throw InvalidParameter(parameterName)
    return parameter
}