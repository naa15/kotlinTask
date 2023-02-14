class CompareTrades {
    companion object : Comparator<Trade> {

        override fun compare(a: Trade, b: Trade): Int {
            return (a.getPrice()*a.getQuantity() - b.getPrice()*b.getQuantity()).toInt()
        }
    }
}