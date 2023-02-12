class Trade constructor (
    private var type: String? = null,
    private var version: Int = 0,
    private var date: String? = null,
    private var direction: String? = null,
    private var itemID: String? = null,
    private var price: Double = 0.0,
    private var quantity: Int = 0,
    private var buyer: String? = null,
    private var seller: String? = null,
//    private var nested: String? = null,
//    private var value: Double = 0.0,
    private var comment: String? = null
) {
//    fun getValue(): Double {
//        return value
//    }

    fun getPrice(): Double {
        return price
    }

    fun getQuantity(): Int {
        return quantity
    }

    fun getVersion(): Int {
        return version
    }

    fun getBuyer(): String? {
        return buyer
    }

    fun getComment(): String? {
        return comment
    }

    fun getDate(): String? {
        return date
    }

    fun getDirection(): String? {
        return direction
    }

    fun getItemID(): String? {
        return itemID
    }

//    fun getNested(): String? {
//        return nested
//    }

    fun getSeller(): String? {
        return seller
    }

    fun getType(): String? {
        return type
    }

//    operator fun compareTo(trade: Trade): Int {
//        return if (value > trade.value) {
//            1
//        } else if (value < trade.value) {
//            -1
//        } else 0
//    }
}
