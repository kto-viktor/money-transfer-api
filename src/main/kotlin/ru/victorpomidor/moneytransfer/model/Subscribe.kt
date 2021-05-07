package ru.victorpomidor.moneytransfer.model

import java.util.UUID

data class Subscribe(
    val accountId: UUID? = null,
    val token: String,
    val callbackUrl: String
)
