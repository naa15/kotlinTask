class CompareTrades {
    companion object : Comparator<Trade> {

        override fun compare(a: Trade, b: Trade): Int {
            return (a.price * a.quantity.toBigDecimal() - b.price * b.quantity.toBigDecimal()).toInt()
        }
    }
}