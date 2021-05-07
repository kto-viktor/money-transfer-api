package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Subscribe
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemorySubscribeService(private val accountService: AccountService) : SubscribeService {
    private val subscribes: MutableMap<UUID, Subscribe> = ConcurrentHashMap()

    override fun subscribe(accountId: UUID, subscribe: Subscribe) {
        require(accountService.getAccount(accountId)?.token == subscribe.token) { "Wrong token provided" }
        subscribes[accountId] = subscribe
    }

    override fun getSubscribe(accountId: UUID): Subscribe? = subscribes[accountId]
}
