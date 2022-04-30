package pt.isel.ls.utils

import org.http4k.core.Request
import org.http4k.core.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> getLoggerFor(): Logger = LoggerFactory.getLogger(T::class.java)

fun Logger.infoLogRequest(request: Request) {
    info(
        "[REQUEST]: method={}, uri={}, content-type={} accept={}",
        request.method,
        request.uri,
        request.header("content-type"),
        request.header("accept")
    )
}

inline fun Logger.traceFunction(functionName: String, args: () -> List<Pair<String, String?>> = { emptyList() }) =
    trace(
        "Entered $functionName with the following arguments: " +
            args().joinToString(", ") { "${it.first}: ${it.second}" }
    )

fun Logger.warnStatus(status: Status, message: String) =
    warn("[STATUS]:$status with : $message")
