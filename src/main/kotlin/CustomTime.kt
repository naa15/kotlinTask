class CustomTime (
    public var hours : Int = 0,
    public var minutes : Int = 0,
    public var seconds : Int = 0,
) {
    public override fun toString(): String {
        return ("" + hours + " hours " + minutes + " minutes " + seconds + " seconds")
    }
}