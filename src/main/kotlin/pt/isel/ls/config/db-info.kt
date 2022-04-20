package pt.isel.ls.config

data class DbConnectionInfo(val url: String, val user: String, val password: String, val dataBase: String)

fun dbConnectionInfo(): DbConnectionInfo {
    val requireEnvVariable = { name: String ->
        System.getenv(name) ?: error("Please specify $name environment variable")
    }

    return DbConnectionInfo(
        url = requireEnvVariable("JDBC_DATABASE_URL"),
        user = requireEnvVariable("JDBC_DATABASE_USER"),
        password = requireEnvVariable("JDBC_DATABASE_PASSWORD"),
        dataBase = requireEnvVariable("JDBC_DATABASE_NAME")
    )
}
