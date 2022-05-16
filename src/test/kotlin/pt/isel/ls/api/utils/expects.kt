package pt.isel.ls.api.utils

import org.http4k.core.Response
import org.http4k.core.Status
import kotlin.test.assertEquals

/**
 * Ensure that the response has the OK status code.
 */
fun Response.expectOK(): Response {
    assertEquals(Status.OK, this.status)
    return this
}

/**
 * Ensure that the response has the CREATED status code.
 */
fun Response.expectCreated(): Response {
    assertEquals(Status.CREATED, this.status)
    return this
}

/**
 * Ensure that the response has the NOT_FOUND status code.
 */
fun Response.expectNotFound(): Response {
    assertEquals(Status.NOT_FOUND, this.status)
    return this
}

/**
 * Ensure that the response has the BAD_REQUEST status code.
 */
fun Response.expectBadRequest(): Response {
    assertEquals(Status.BAD_REQUEST, this.status)
    return this
}

/**
 * Ensure that the response has the UNAUTHORIZED status code.
 */
fun Response.expectUnauthorized(): Response {
    assertEquals(Status.UNAUTHORIZED, this.status)
    return this
}

/**
 * Ensure that the response has the FORBIDDEN status code.
 */
fun Response.expectForbidden(): Response {
    assertEquals(Status.FORBIDDEN, this.status)
    return this
}

/**
 * Ensure that the response has the NO_CONTENT status code.
 */
fun Response.expectNoContent(): Response {
    assertEquals(Status.NO_CONTENT, this.status)
    return this
}
