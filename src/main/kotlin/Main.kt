import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.math.BigDecimal
import java.nio.file.Paths
import kotlin.io.path.bufferedReader


var list = mutableListOf<Trade>()
var set = mutableSetOf<String>()
var result = mutableListOf<String>()
var map = mutableMapOf<String, BigDecimal>()
fun main() {
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
        val itemID = csvRecord.get(i++)
        val price = csvRecord.get(i++)
        val quantity = csvRecord.get(i++)
        val seller = csvRecord.get(i++)
        val buyer = csvRecord.get(i++)
        set.add(buyer)
        set.add(seller)
        val comment = csvRecord.get(i)

        val t = Trade(type, 0, dateTime, direction, itemID, price.toBigDecimal(), quantity.toInt(), buyer, seller, comment)
        if(map.containsKey(itemID)) {
            map[itemID] = map[itemID]!! + price.toBigDecimal() * quantity.toBigDecimal()
        } else {
            map[itemID] = price.toBigDecimal() * quantity.toBigDecimal()
        }
        list.add(t)
    }
}
fun numberOfTrades() {
    var count = 0
    for (i in list) {
        if (i.type == "Trade") {
            count++
        }
    }
    result.add("Number of trades is: $count")
}

fun numberOfExtendedTrades() {
    var count = 0
    for (i in list) {
        if (i.type != "Trade") {
            count++
        }
    }
    result.add("Number of extended trades is: $count")
}

fun totalValueOfBuyTrades() {
    var sum = BigDecimal(0)
    for (i in list) {
        if(i.direction == "B") {
            sum += (i.quantity.toBigDecimal() * i.price)
        }
    }
    result.add("Total value of BUY trades and extended trades is $sum")
}

fun totalValueOfSellTrades() {
    var sum = BigDecimal(0)
    for (i in list) {
        if(i.direction == "S") {
            sum += (i.quantity.toBigDecimal() * i.price)
        }
    }
    result.add("Total value of SELL trades and extended trades is $sum")
}

fun lengthOfTheLongestComment() {
    var count = 0
    var comment : String? = ""
    for (i in list) {
        if(i.type == "Trade") {
            val length : Int = i.comment?.length ?: 0
            if(length  > count) {
                count = length
                comment = i.comment
            }
        }
    }

    result.add("Length of the longest comment is: $count")
    result.add("The longest comment is: $comment")
}

fun numberOfFirmIDs() {
    result.add("Total number of unique firms: " + set.size)
}

fun listUniqueFirms() {
    result.add("List of firms IDs: ")
    var res = ""
    set.forEach { res+=("$it|") }
    result.add(res)
}

fun listProductIDs() {
    result.add("List total values per item ID: ")
    map.forEach { result.add(it.key + " " + it.value) }
}

fun tradeInterval() {
    val firstTime = list[0].date.substring(list[0].date.length - 12)
    val last = list[list.size - 1].date.substring(list[list.size - 1].date.length - 12)
    val start = CustomTime(firstTime.substring(0,2).toInt(), firstTime.substring(3,5).toInt(), firstTime.substring(6,8).toInt())
    val end = CustomTime(last.substring(0,2).toInt(), last.substring(3,5).toInt(), last.substring(6,8).toInt())
    result.add("Time interval between the first and the last trades: ")
    result.add(difference(start, end).toString())
}

// using the function from https://www.programiz.com/kotlin-programming/examples/difference-time
fun difference(start: CustomTime, stop: CustomTime): CustomTime {
    val diff = CustomTime(0, 0, 0)

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

    val tx = 100F
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