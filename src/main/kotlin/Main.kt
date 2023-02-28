import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.nio.file.Paths
import kotlin.io.path.bufferedReader

var list = ArrayList<Trade>()
var set = mutableSetOf<String>()
fun main(args: Array<String>) {
    readWithCSVParser()
//    readWithoutCSVParser()
    println(" * * * SUMMARY * * * ")
    numberOfTrades()
    numberOfExtendedTrades()
    totalValueOfBuyTrades()
    totalValueOfSellTrades()
    lengthOfTheLongestComment()
    numberOfFirmIDs()
    listUniqueFirms()
    listProductIDs()
}

fun readWithCSVParser() {
    val bufferedReader = Paths.get("input.csv").bufferedReader()
    val csvParser = CSVParser(bufferedReader, CSVFormat.DEFAULT)

    for (csvRecord in csvParser) {
        var i = 0
        val type = csvRecord.get(i++)
        if(type.equals("Extended trade")) {
            i++
        }
        val dateTime = csvRecord.get(i++)
        val direction = csvRecord.get(i++)
        var itemID = csvRecord.get(i++)
        var price = csvRecord.get(i++)
        var quantity = csvRecord.get(i++)
        var seller = csvRecord.get(i++)
        var buyer = csvRecord.get(i++)
        set.add(buyer)
        set.add(seller)
        var comment = csvRecord.get(i++)

        val t : Trade = Trade(type, 0, dateTime, direction, itemID, price.toDouble(), quantity.toInt(), buyer, seller, comment)
        list.add(t)

//        println(type + " " + dateTime + " " + direction + " " + itemID + " " + price + " " + quantity + " " + seller + " " + buyer + " " + comment)
    }
}
fun numberOfTrades() {
    var count : Int = 0
    for (i in list) {
        if (i.getType().equals("Trade")) {
            count++
        }
    }
    println("Number of trades is: " + count)
}

fun numberOfExtendedTrades() {
    var count : Int = 0
    for (i in list) {
        if (! i.getType().equals("Trade")) {
            count++
        }
    }
    println("Number of extended trades is: " + count)
}

fun totalValueOfBuyTrades() {
    var sum : Double = 0.0
    for (i in list) {
        if(i.getDirection().equals("B")) {
            sum += (i.getQuantity() * i.getPrice())
        }
    }
    println("Total value of BUY trades and extended trades is " + sum)
}

fun totalValueOfSellTrades() {
    var sum : Double = 0.0
    for (i in list) {
        if(i.getDirection().equals("S")) {
            sum += (i.getQuantity() * i.getPrice())
        }
    }
    println("Total value of SELL trades and extended trades is " + sum)
}

fun lengthOfTheLongestComment() {
    var count : Int = 0
    var comment : String? = ""
    for (i in list) {
        if(i.getType().equals("Trade")) {
            var length : Int = i.getComment()?.length ?: 0
            if(length  > count) {
                count = length
                comment = i.getComment()
            }
        }
    }

    println("Length of the longest comment is: " + count)
    println("The longest comment is: " + comment)
}

fun numberOfFirmIDs() {
    println("Total number of unique firms: " + set.size)
}

fun listUniqueFirms() {
    println("List of firms IDs: ")
    set.forEach { print(it + "|") }
    println()
}

fun listProductIDs() {
    println("List product IDs in ascending order along with their values: ")
    list.sortedWith(CompareTrades).forEach() { println(it.getItemID() + " " + it.getPrice()*it.getQuantity()) }
}
fun readWithoutCSVParser() {
    val file = File("trades.csv")
    try {
        BufferedReader(FileReader(file)).use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                if (line.equals("")) {
                    line = br.readLine()
                    if (line == null) break;
                }
                var type = line
                var version = ""
                if(! type.equals("Trade")) {
                    version = br.readLine()
                }
                var date: String? = br.readLine()
                var direction: String? = br.readLine()
                var itemID: String? = br.readLine()
                var price = br.readLine()
                var quantity = br.readLine()
                var buyer: String = br.readLine()
                var seller: String = br.readLine()
                set.add(buyer)
                set.add(seller)
                var comment = ""
                if(type.equals("Trade")) {
                    comment = br.readLine()
                }
                var nested = ""
                if(! type.equals("Trade")) {
                    while (! br.readLine().equals("")) {
                        nested += ""
                    }
                }
                val t : Trade = Trade(type, 0, date, direction, itemID, price.toDouble(), quantity.toInt(), buyer, seller, comment)
                list.add(t)
//                println(type + " " + date + " " + direction + " " + itemID + " " + price + " " + quantity + " " + buyer + " " +seller + " " +comment )
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}