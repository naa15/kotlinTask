import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.math.BigDecimal
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant
import kotlin.io.path.bufferedReader


val tradeList = mutableListOf<Trade>()
val setOfBuyersAndSellers = mutableSetOf<String>()
val result = mutableListOf<String>()
val valuesOfItemsMap = mutableMapOf<String, BigDecimal>()
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
    listTotalsPerItemIDs()
    writeInPDF()
}

fun readWithCSVParser() {
    val bufferedReader = Paths.get("input.csv").bufferedReader()
    val csvParser = CSVParser(bufferedReader, CSVFormat.DEFAULT)

    for (csvRecord in csvParser) {
        var index = 0
        val type = csvRecord.get(index++)
        if(type == "Extended trade") {
            index++
        }
        val dateTime = csvRecord.get(index++)
        val dateTimeInstant: Instant = Instant.parse(dateTime.substring(0,4) + "-" + dateTime.substring(5,7) + "-" + dateTime.substring(8,10) + "T" + dateTime.substring(11,19) + "." + dateTime.substring(20) + "Z")
        var direction = csvRecord.get(index++)
        if(direction == "BUY_") {
            direction = "B"
        } else if (direction == "SELL") {
            direction = "S"
        }
        val itemID = csvRecord.get(index++)
        val price = csvRecord.get(index++)
        val quantity = csvRecord.get(index++)
        val seller = csvRecord.get(index++)
        val buyer = csvRecord.get(index++)
        setOfBuyersAndSellers.add(buyer)
        setOfBuyersAndSellers.add(seller)
        val comment = csvRecord.get(index)

        val trade = Trade(type, dateTimeInstant, direction, price.toBigDecimal(), quantity.toBigDecimal(), comment)
        if(valuesOfItemsMap.containsKey(itemID)) {
            valuesOfItemsMap[itemID] = valuesOfItemsMap[itemID]!! + trade.price * trade.quantity
        } else {
            valuesOfItemsMap[itemID] = trade.price * trade.quantity
        }
        tradeList.add(trade)
    }
}
fun numberOfTrades() {
    var count = 0
    for (i in tradeList) {
        if (i.type == "Trade") {
            count++
        }
    }
    result.add("Number of trades is: $count")
}

fun numberOfExtendedTrades() {
    var count = 0
    for (i in tradeList) {
        if (i.type != "Trade") {
            count++
        }
    }
    result.add("Number of extended trades is: $count")
}

fun totalValueOfBuyTrades() {
    var sum = BigDecimal(0)
    for (i in tradeList) {
        if(i.direction == "B") {
            sum += (i.quantity * i.price)
        }
    }
    result.add("Total value of BUY trades and extended trades is $sum")
}

fun totalValueOfSellTrades() {
    var sum = BigDecimal(0)
    for (i in tradeList) {
        if(i.direction == "S") {
            sum += (i.quantity * i.price)
        }
    }
    result.add("Total value of SELL trades and extended trades is $sum")
}

fun lengthOfTheLongestComment() {
    var count = 0
    var comment : String? = ""
    for (i in tradeList) {
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
    result.add("Total number of unique firms: " + setOfBuyersAndSellers.size)
}

fun listUniqueFirms() {
    result.add("List of firms IDs: ")
    var res = ""
    setOfBuyersAndSellers.forEach { res+=("$it|") }
    result.add(res)
}

fun listTotalsPerItemIDs() {
    result.add("List total values per item ID: ")
    val sortedMap = valuesOfItemsMap.toList().sortedBy { (_, value) -> value}.toMap()
    sortedMap.forEach { result.add(it.key + " " + it.value) }
}

fun tradeInterval() {
    var start : Instant = Instant.MAX
    var end : Instant = Instant.MIN

    for (i in tradeList) {
        if (i.date.isBefore(start)) {
            start = i.date
        }
        if (i.date.isAfter(end)) {
            end = i.date
        }
    }

    val timeElapsed: Duration = Duration.between(start, end)

    result.add("Time interval between the first and the last trades: ")
    result.add(timeElapsed.seconds.toString())
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