import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.nio.file.Paths
import kotlin.io.path.bufferedReader


var list = ArrayList<Trade>()
var set = mutableSetOf<String>()
var result = ArrayList<String>()
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
    numberOfFirmIDs()
    result.add("")
    listUniqueFirms()
    result.add("")
    listProductIDs()
    result.add("")
    writeInPDF()
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
    }
}
fun numberOfTrades() {
    var count : Int = 0
    for (i in list) {
        if (i.getType().equals("Trade")) {
            count++
        }
    }
    result.add("Number of trades is: " + count)
}

fun numberOfExtendedTrades() {
    var count : Int = 0
    for (i in list) {
        if (! i.getType().equals("Trade")) {
            count++
        }
    }
    result.add("Number of extended trades is: " + count)
}

fun totalValueOfBuyTrades() {
    var sum : Double = 0.0
    for (i in list) {
        if(i.getDirection().equals("B")) {
            sum += (i.getQuantity() * i.getPrice())
        }
    }
    result.add("Total value of BUY trades and extended trades is " + sum)
}

fun totalValueOfSellTrades() {
    var sum : Double = 0.0
    for (i in list) {
        if(i.getDirection().equals("S")) {
            sum += (i.getQuantity() * i.getPrice())
        }
    }
    result.add("Total value of SELL trades and extended trades is " + sum)
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

    result.add("Length of the longest comment is: " + count)
    result.add("The longest comment is: " + comment)
}

fun numberOfFirmIDs() {
    result.add("Total number of unique firms: " + set.size)
}

fun listUniqueFirms() {
    result.add("List of firms IDs: ")
    set.forEach { result.add((it + "|")) }
}

fun listProductIDs() {
    result.add("List product IDs in ascending order along with their values: ")
    list.sortedWith(CompareTrades).forEach() { result.add(it.getItemID() + " " + it.getPrice()*it.getQuantity()) }
}

fun writeInPDF() {
    val document = PDDocument()
    val page = PDPage()
    document.addPage(page)

    val font: PDFont = PDType1Font.HELVETICA_BOLD
    val contentStream = PDPageContentStream(document, page)

    var tx = 100F
    var ty = 700F
    for (line in result) {
        contentStream.beginText()
        contentStream.setFont(font, 12F)
        contentStream.moveTextPositionByAmount(tx, ty)
        contentStream.drawString(line)
        contentStream.endText()
        ty -= 20F
    }

    contentStream.close()

    document.save("Summary.pdf")
    document.close()
}