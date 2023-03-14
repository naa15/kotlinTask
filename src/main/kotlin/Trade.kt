import java.math.BigDecimal
import java.time.Instant

class Trade constructor (
    val type: String? = null,
    val date: Instant,
    val direction: String? = null,
    val price: BigDecimal = BigDecimal(0),
    val quantity: BigDecimal = BigDecimal(0),
    val comment: String? = null
) {
    operator fun compareTo(trade: Trade): Int {
        return if ((price * quantity) > (trade.price * trade.quantity)) {
            1
        } else if ((price * quantity) < (trade.price * trade.quantity)) {
            -1
        } else 0
    }
}
