package pt.isel.ls.http

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.Test
import kotlin.test.assertEquals

class ApiIntegration {


    private val backend = getApiRoutes()
    private fun Response.expectOK(): Response {
        assertEquals(this.status,Status.OK)
        return this
    }
    private fun Response.expectCreated(): Response {
        assertEquals(this.status,Status.CREATED)
        return this
    }

    @Test
    fun `creation of a user`() {
        val baseRequest = Request(Method.POST, "/api/users")
        val request = baseRequest.body("""{ name: "abc", email: "abc@gmail.com" }""")

        val response = backend(request).expectCreated()
        // assertEquals(response.bodyString(), )

    }

    @Test
    fun `getting all users`() {
        val request = Request(Method.GET, "/users")

        val response = backend(request).expectOK()
        // assertEquals(response.bodyString(), )

    }

    @Test
    fun `getting a specific users`() {
        val baseRequest = Request(Method.GET, "/users")
        val request = baseRequest.query ("id",TODO())

        val response = backend(request).expectOK()
        // assertEquals(response.bodyString(), )

    }


}

