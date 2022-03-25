package pt.isel.ls.http.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

/**
 * Makes a get request from [backend] handler
 * and tries to parse the response body to a [T] object, calling an expect function
 * which ensures that the response has a determined status code.
 *
 * Generic parameter is the response body expected type.
 * This type must be serializable.
 */
inline fun <reified ResponseBody> getRequest(
    backend: HttpHandler,
    uri: String,
    expectedStatus: Response.() -> Response
): ResponseBody =
    Json.decodeFromString(
        backend(Request(Method.GET, uri))
            .expectedStatus()
            .bodyString()
    )

/**
 * Makes a post request from [backend] handler sending [body]
 * and tries to parse the response body to a [ResponseBody] object, calling an expect function
 * which ensures that the response has a determined status code.
 *
 * Generic parameters are respectively the request body type and the response body expected type.
 * Both of these types must be serializable.
 */
inline fun <reified RequestBody, reified ResponseBody> postRequest(
    backend: HttpHandler,
    uri: String,
    body: RequestBody,
    expectedStatus: Response.() -> Response
): ResponseBody =
    Json.decodeFromString(
        backend(
            Request(Method.POST, uri).body(Json.encodeToString(body))
        )
            .expectedStatus()
            .bodyString()
    )
