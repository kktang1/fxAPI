package kathy.app // name for everything in the src file

import io.ktor.application.Application
import io.ktor.features.*
import io.ktor.application.install
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

// full name would be kathy.app.main(....)
// input Array<String> output Unit
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args) //  entry point - main

fun Application.module() { // import above
    install(CallLogging) { level = Level.INFO } // logging the data ------ LEVEL.INFO???????
    install(ContentNegotiation) { // concert the format into json
        json() // json format
    }
    val instance = FXRateService()
    // Routing is a feature that is installed into an Application to simplify and structure page request handling.
    routing {currencyRouting(instance)}

    //connect to database
    Database.connect("jdbc:postgresql://localhost:5432/postgres?user=admin&password=admin", driver = "org.postgresql.Driver")

    // making use of the instance
    instance.createTable()

}

val log = LoggerFactory.getLogger(Application::class.java)

//

