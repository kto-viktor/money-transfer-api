package ru.victorpomidor.moneytransfer.config

import ru.victorpomidor.moneytransfer.repository.TopUpRepository
import ru.victorpomidor.moneytransfer.repository.TransferRepository
import ru.victorpomidor.moneytransfer.resource.AccountResource
import ru.victorpomidor.moneytransfer.resource.TransferResource
import ru.victorpomidor.moneytransfer.service.InMemoryAccountService
import ru.victorpomidor.moneytransfer.service.TopUpServiceImpl
import ru.victorpomidor.moneytransfer.service.TransferServiceImpl

class DiConfig {
    private val accountService = InMemoryAccountService()
    private val topUpService = TopUpServiceImpl(accountService, TopUpRepository())
    private val transferService = TransferServiceImpl(accountService, TransferRepository())

    val accountResource = AccountResource(accountService, topUpService)
    val transferResource = TransferResource(transferService)
}
