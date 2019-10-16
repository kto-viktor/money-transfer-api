import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

fun Any.entity(): Entity<Any> = Entity.entity(this, MediaType.APPLICATION_JSON_TYPE)

inline fun <reified T> Response.readBody(): T {
    return jacksonObjectMapper().readValue(this.readEntity(String::class.java))
}
