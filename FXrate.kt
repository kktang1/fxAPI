package kathy.app

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

// creation of an empty table
object FXRates_test: IntIdTable() {
    // primary key cannot alter
    val rate = double("rate")
    val country1 = varchar("country1", 4)
    val country2 = varchar("country2",4)
}

// a sngle entry(row) in the table- define
class FXRate(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FXRate>(FXRates_test)
    var rate by FXRates_test.rate
    var country1 by FXRates_test.country1
    var country2 by FXRates_test.country2
    override fun toString(): String {
        return "FX_rate(id=$id, country1=$country1, country2=$country2, rate=$rate)"
    }
}

class FXRateService {

    fun createTable () {
        transaction {
            SchemaUtils.create(FXRates_test)
        }
    }

    fun fxEntry(curr1: String, curr2:String, result: Double){
        transaction {
            addLogger(StdOutSqlLogger)

            //check
            if((FXRates_test.select{FXRates_test.country1 eq curr1 and(FXRates_test.country2 eq curr2)}.empty())){
                // insert
                FXRate.new {
                    country1 = curr1
                    country2 = curr2
                    rate = result
                }
            }
            else{
                // update + where location
                FXRates_test.update ({FXRates_test.country1 eq curr1 and(FXRates_test.country2 eq curr2)}) {
                    it[FXRates_test.rate] = result
                }
            }
            }
        }

    fun requestDATA(){
        val kathyList = transaction {
           // println("Value: ${FXRate.find{FXRate.country1 eq "GBP"}.joinToString{it.country2})}
            FXRates_test.select{FXRates_test.country1 eq "SGD"}.toList()
        }
        // write to a file so y        print(kathyList)ou can see..


        kathyList.forEach {
            println(it)}
        
        //writeToFile(kathyList)
    }



    }




