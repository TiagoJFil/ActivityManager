package pt.isel.ls.utils

import org.http4k.core.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory


inline fun <reified T>getLoggerFor(): Logger = LoggerFactory.getLogger(T::class.java)


fun Logger.infoLogRequest(request: Request){
    info(
        "[REQUEST RECEIVED]: method={}, uri={}, content-type={} accept={}",
        request.method,
        request.uri,
        request.header("content-type"),
        request.header("accept")
    )
}

fun Logger.traceFunction(functionName: String, vararg args: String? )=
    trace("Entered $functionName with the following arguments: ${args.asList()}")

