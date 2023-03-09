import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.math.BigDecimal
import java.nio.file.Paths
import java.sql.Time
import kotlin.io.path.bufferedReader


var list = mutableListOf<Trade>()
var set = mutableSetOf<String>()
var result = mutableListOf<String>()
var map = mutableMapOf<String, BigDecimal>()
fun main(args: Array<String>) {
    readWithCSVParser()
    result.add(" * * * SUMMARY * * * ")
    result.add("")
    numberOfTrades()
    result.add("")
    numberOfExtendedTrades()
    result.add("")
    totalValueOfBuyTrades()
    result.add("")
    totalValueOfSellTrades()
    result.add("")
    lengthOfTheLongestComment()
    result.add("")
    tradeInterval()
    result.add("")
    numberOfFirmIDs()
    result.add("")
    listUniqueFirms()
    result.add("")
    listProductIDs()
    writeInPDF()
}

fun readWithCSVParser() {
    val bufferedReader = Paths.get("input.csv").bufferedReader()
    val csvParser = CSVParser(bufferedReader, CSVFormat.DEFAULT)

    for (csvRecord in csvParser) {
        var i = 0
        val type = csvRecord.get(i++)
        if(type == "Extended trade") {
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

        val t : Trade = Trade(type, 0, dateTime, direction, itemID, price.toBigDecimal(), quantity.toInt(), buyer, seller, comment)
        if(map.containsKey(itemID)) {
            map[itemID] = map[itemID]!! + price.toBigDecimal() * quantity.toBigDecimal()
        } else {
            map.put(itemID, price.toBigDecimal() * quantity.toBigDecimal())
        }
        list.add(t)
    }
}
fun numberOfTrades() {
    var count : Int = 0
    for (i in list) {
        if (i.type == "Trade") {
            count++
        }
    }
    result.add("Number of trades is: " + count)
}

fun numberOfExtendedTrades() {
    var count : Int = 0
    for (i in list) {
        if (i.type != "Trade") {
            count++
        }
    }
    result.add("Number of extended trades is: " + count)
}

fun totalValueOfBuyTrades() {
    var sum : BigDecimal = BigDecimal(0)
    for (i in list) {
        if(i.direction == "B") {
            sum += (i.quantity.toBigDecimal() * i.price)
        }
    }
    result.add("Total value of BUY trades and extended trades is " + sum)
}

fun totalValueOfSellTrades() {
    var sum : BigDecimal = BigDecimal(0)
    for (i in list) {
        if(i.direction == "S") {
            sum += (i.quantity.toBigDecimal() * i.price)
        }
    }
    result.add("Total value of SELL trades and extended trades is " + sum)
}

fun lengthOfTheLongestComment() {
    var count : Int = 0
    var comment : String? = ""
    for (i in list) {
        if(i.type == "Trade") {
            var length : Int = i.comment?.length ?: 0
            if(length  > count) {
                count = length
                comment = i.comment
            }
        }
    }

    result.add("Length of the longest comment is: " + count)
    result.add("The longest comment is: " + comment)
}

fun numberOfFirmIDs() {
    result.add("Total number of unique firms: " + set.size)
}

fun listUniqueFirms() {
    result.add("List of firms IDs: ")
    var res = ""
    set.forEach { res+=(it + "|") }
    result.add(res)
}

fun listProductIDs() {
    result.add("List total values per item ID: ")
    map.forEach() { result.add(it.key + " " + it.value) }
}

fun tradeInterval() {
    var firstTime = list.get(0).date.substring(list.get(0).date.length - 12)
    var last = list.get(list.size - 1).date.substring(list.get(list.size - 1).date.length - 12)
    val start = Time(firstTime.substring(0,2).toInt(), firstTime.substring(3,5).toInt(), firstTime.substring(6,8).toInt())
    val end = Time(last.substring(0,2).toInt(), last.substring(3,5).toInt(), last.substring(6,8).toInt())
    result.add("Time interval between the first and the last trades: ")
    result.add(difference(start, end).toString())
}

// using the function from https://www.programiz.com/kotlin-programming/examples/difference-time
fun difference(start: Time, stop: Time): Time {
    val diff = Time(0, 0, 0)

    if (stop.seconds > start.seconds) {
        --start.minutes
        start.seconds += 60
    }

    diff.seconds = start.seconds - stop.seconds
    if (stop.minutes > start.minutes) {
        --start.hours
        start.minutes += 60
    }

    diff.minutes = start.minutes - stop.minutes
    diff.hours = start.hours - stop.hours

    return diff
}
fun writeInPDF() {
    val document = PDDocument()
    val page = PDPage()
    document.addPage(page)

    val font: PDFont = PDType1Font.COURIER
    val contentStream = PDPageContentStream(document, page)

    var tx = 100F
    var ty = 700F
    for (line in result) {
        contentStream.beginText()
        contentStream.setFont(font, 12F)
        contentStream.newLineAtOffset(tx, ty)
        contentStream.showText(line)
        contentStream.endText()
        ty -= 20F
    }

    contentStream.close()

    document.save("Summary.pdf")
    document.close()
}