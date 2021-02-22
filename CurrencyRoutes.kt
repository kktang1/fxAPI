package kathy.app

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.text.get
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.util.pipeline.*
import io.netty.handler.codec.http2.UniformStreamByteDistributor
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun Route.currencyRouting(instance: FXRateService) {
    route("/currency") { // http request -- need to match the call name
        // postman
        get {
            val client = HttpClient(OkHttp) // access the http??


            //context == information exchange
            // look up "this" -----------------------------------
            //change the name kathy-----------------------------
            //val kathy: String = this.context.request.queryParameters["kathy"]!! // ? == null?, !! = dc, .get = [], ?: "default value"if user uses the API wrong
            //val kathy: String = this.context.request.queryParameters["kathy"] ?: "100"
            val quantity: String? = this.context.request.queryParameters["kathy"]

            if (quantity == null) {
                call.respond("Please input the correct quantity value")
                return@get // break???
            }

            val curr1: String = this.context.request.queryParameters["curr1"]!!
            val curr2: String = this.context.request.queryParameters["curr2"]!!

            if (curr1 == null || curr2 == null) {
                call.respond("Please input a valid currency type")
                return@get
            }

            // to check if the value is already available in cache, if so use that instead and break

            val cachedExchangeRate = cache[Currencies(curr1, curr2)]

            if (cachedExchangeRate != null) {
                call.respond(cachedExchangeRate * quantity.toDouble())
                println("YO I AM IN") // prove that its in cache
                return@get
            }

//            if (cache.contains(Currencies(curr1,curr2))) {
//                call.respond(cache[Currencies(curr1,curr2)]!!)
//            }
//
//            cache[Currencies(curr1,curr2)]?.let {
//                call.respond(it)
//            }

            // call.respond(cache[Currencies(curr1, curr2)]!!)

            // ("SGD","MYR","EUR","USD","AUD","JPY","CNH","HKD","CAD","INR","DKK","GBP","RUB","NZD","MXN","IDR","TWD","THB","VND")
            val quotes = client.get<String>("https://currency-exchange.p.rapidapi.com/listquotes") {
                header("x-rapidapi-key", "eec0820af1mshf2ca5064c4efe6dp1a7901jsn560024a12844")
                header("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
            }

            // [a, b, c, null]
            // null !in [a.b.c] == true, "", " "
            if (curr1 !in quotes || curr2 !in quotes) {
                call.respond("Currency is not available")
                return@get
            }

            // $ variable
            val result =
                client.get<String>("https://currency-exchange.p.rapidapi.com/exchange?from=$curr1&to=$curr2&q=1.0") {
                    header("x-rapidapi-key", "eec0820af1mshf2ca5064c4efe6dp1a7901jsn560024a12844")
                    header("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
                }

            //call.respond(result.toDouble()) // change int into double

            //var double = result.toDouble().times(kathy.toDouble())

            call.respond(result.toDouble() * quantity.toDouble()) // only one call allowed, -= return (python)

            // localhost:8080/currency?kathy=100&curr1=GBP&curr2=SGD

            cache[Currencies(curr1, curr2)] = result.toDouble()

        }
    }

//    val quotes = client.get<String>("https://currency-exchange.p.rapidapi.com/listquotes") {
//        header("x-rapidapi-key", "eec0820af1mshf2ca5064c4efe6dp1a7901jsn560024a12844")
//        header("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
//    }
//
//    val result =
//        client.get<String>("https://currency-exchange.p.rapidapi.com/exchange?from=$curr1&to=$curr2&q=1.0") {
//            header("x-rapidapi-key", "eec0820af1mshf2ca5064c4efe6dp1a7901jsn560024a12844")
//            header("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
//        }

    route("/exchangeRates") {
        get {
            val client = HttpClient(OkHttp)

            val quotes = client.get<String>("https://currency-exchange.p.rapidapi.com/listquotes") {
                header("x-rapidapi-key", "eec0820af1mshf2ca5064c4efe6dp1a7901jsn560024a12844")
                header("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
            }
            val list = quotes.replace("\"", "").replace("[", "").replace("]", "").split(",")
//            val list = quotes.split(",")
            // println(quotes)
            // println(list)


            // for loop -- later

//            for (element in list) {
//
//            }

            for (index_1 in list.indices) {
                for (index_2 in list.indices) {
                    val curr1 = list[index_1]
                    val curr2 = list[index_2]

                    if (curr1 == curr2) {
                        continue
                    }
                    // save the corresponding fx rate in the file, for lop for each fx, size = 19, 722

                    val result =
                        client.get<String>("https://currency-exchange.p.rapidapi.com/exchange?from=$curr1&to=$curr2&q=1.0") {
                            header("x-rapidapi-key", "eec0820af1mshf2ca5064c4efe6dp1a7901jsn560024a12844")
                            header("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
                        }

                    // store in txt
                    //writeToFile(curr1, curr2, result.toDouble()) // call file.kt

                    instance.fxEntry(curr1, curr2, result.toDouble())
                }
            }
            // add the sql request here

        }
    }

    route("/getdata") {
        get{
            instance.requestDATA()
        }
    }

}


val cache = mutableMapOf<Currencies, Double>()

data class Currencies(val curr1: String, val curr2: String)

// bondary for currency quotes - differnet currency differences

class MyClass {

    private val a = "hi"

    fun myFunction() {
        this.a
    }
}

@ContextDsl
public fun Route.get(body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return method(HttpMethod.Get) { handle(body) }
}

// export it into text file
//// for loop for all fx
//// display cache