package ru.victorpomidor.moneytransfer.model

import java.math.BigDecimal
import java.util.UUID

data class Account(
    val id: UUID,
    val name: String,
    val balance: BigDecimal = BigDecimal.ZERO,
    val currency: String,
    val token: String
)
