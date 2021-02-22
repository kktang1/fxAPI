package kathy.app

import org.jetbrains.exposed.sql.ResultRow
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.collections.AbstractList as AbstractList1

fun main(){
    val fileName = "src/main/resources/kathyFile.txt" // directory
    val kathyFile =File(fileName) // creation of empty file

    // adjustment in the file created
    kathyFile.printWriter().use { out ->
        out.println("First line")
        out.println("Second line")
    }
    println("Writed to file")

}


// overwrite it theres an existing value

fun writeToFile(kathyList: List<ResultRow>) {

    val path = "src/main/resources/kathyListing.txt"
//    val text = "[$curr1, $curr2, $result]\n"
//        val fw = FileWriter(path, true)
//        fw.write(text)
//        fw.close()
//    val input = kathyList
//        val fw = FileWriter(path, true)
//        fw.write(input)
//        fw.close()


    println("I am doneee")

}


// next time
// add in the for loop
// export it in txt in a nice way
// finish off


