import java.math.BigDecimal
import java.time.Instant

class Trade constructor (
    val type: String?,
    val date: Instant,
    val direction: String?,
    val price: BigDecimal,
    val quantity: BigDecimal,
    val comment: String?
)