package ru.victorpomidor.moneytransfer.config

import ru.victorpomidor.moneytransfer.repository.TopUpRepository
import ru.victorpomidor.moneytransfer.repository.TransferRepository
import ru.victorpomidor.moneytransfer.resource.AccountResource
import ru.victorpomidor.moneytransfer.resource.TransferResource
import ru.victorpomidor.moneytransfer.service.AsyncCallbackService
import ru.victorpomidor.moneytransfer.service.InMemoryAccountService
import ru.victorpomidor.moneytransfer.service.InMemorySubscribeService
import ru.victorpomidor.moneytransfer.service.TopUpServiceImpl
import ru.victorpomidor.moneytransfer.service.TransferServiceImpl

class DiConfig {
    private val accountService = InMemoryAccountService()
    private val topUpService = TopUpServiceImpl(accountService, TopUpRepository())
    private var subscribeService = InMemorySubscribeService(accountService)
    private val callbackService = AsyncCallbackService(subscribeService)
    private val transferService = TransferServiceImpl(accountService, TransferRepository(), callbackService)

    val accountResource = AccountResource(accountService, topUpService, subscribeService)
    val transferResource = TransferResource(transferService)
}
