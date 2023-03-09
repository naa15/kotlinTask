import java.math.BigDecimal

class Trade constructor (
    public var type: String? = null,
    public var version: Int = 0,
    public var date: String = "",
    public var direction: String? = null,
    public var itemID: String? = null,
    public var price: BigDecimal = BigDecimal(0),
    public var quantity: Int = 0,
    public var buyer: String? = null,
    public var seller: String? = null,
    public var comment: String? = null
) {
    operator fun compareTo(trade: Trade): Int {
        return if ((price * quantity.toBigDecimal()) > (trade.price * trade.quantity.toBigDecimal())) {
            1
        } else if ((price * quantity.toBigDecimal()) < (trade.price * trade.quantity.toBigDecimal())) {
            -1
        } else 0
    }
}
