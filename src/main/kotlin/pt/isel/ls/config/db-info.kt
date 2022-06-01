package pt.isel.ls.config

data class DbConnectionInfo(val url: String)

fun dbConnectionInfo(): DbConnectionInfo {
    val requireEnvVariable = { name: String ->
        System.getenv(name) ?: error("Please specify $name environment variable")
    }

    return DbConnectionInfo(
        url = requireEnvVariable("JDBC_DATABASE_URL"),
    )
}
