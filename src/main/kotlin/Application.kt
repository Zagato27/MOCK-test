import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.ZonedDateTime
import java.util.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        routing {
            get("/api/users") {
                val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 3
                val users = List(count) {
                    mapOf(
                        "id" to (it + 1),
                        "email" to "mailto:user${it + 1}@reqres.in",
                        "first_name" to "User${it + 1}",
                        "last_name" to "LastName${it + 1}"
                    )
                }
                call.respond(HttpStatusCode.OK, users)
            }

            get("/api/user/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: 1
                val user = mapOf(
                    "id" to id,
                    "email" to "mailto:user$id@reqres.in",
                    "first_name" to "User$id",
                    "last_name" to "LastName$id",
                    "address" to "28 Lenin Street, Moscow"
                )
                call.respond(HttpStatusCode.OK, user)
            }

            post("/api/user") {
                val user = call.receive<Map<String, String>>()
                val response = user.toMutableMap()
                response["id"] = (Random().nextInt(1000) + 1).toString()
                response["createdAt"] = ZonedDateTime.now().toString()
                call.respond(HttpStatusCode.Created, response)
            }
        }
    }.start(wait = true)
}
