package ru.victorpomidor.moneytransfer.model

import java.math.BigDecimal

data class TopUp(
    val requestId: String,
    val systemId: String,
    val amount: BigDecimal,
    val status: TopUpStatus? = null
) {
    fun requestKey() = RequestKey(requestId, systemId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TopUp

        if (requestId != other.requestId) return false
        if (systemId != other.systemId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestId.hashCode()
        result = 31 * result + systemId.hashCode()
        return result
    }
}

enum class TopUpStatus(val code: Int) {
    SUCCESS(0),
    IN_PROGRESS(1),
    ACCOUNT_NOT_FOUND(2)
}
