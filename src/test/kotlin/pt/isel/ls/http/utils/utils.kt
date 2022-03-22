package pt.isel.ls.http.utils

import org.http4k.core.Response
import org.http4k.core.Status
import kotlin.test.assertEquals

fun Response.expectOK(): Response {
    assertEquals(Status.OK,this.status)
    return this
}
fun Response.expectCreated() : Response {
    assertEquals(Status.CREATED,this.status)
    return this
}
fun Response.expectNotFound(): Response {
    assertEquals(Status.NOT_FOUND, this.status)
    return this
}
fun Response.expectBadRequest(): Response {
    assertEquals(Status.BAD_REQUEST, this.status)
    return this
}

fun Response.expectMessage(msg: String) : Response {
    assertEquals(msg,this.bodyString())
    return this
}