package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Subscribe
import java.util.UUID

interface SubscribeService {
    fun subscribe(accountId: UUID, subscribe: Subscribe)

    fun getSubscribe(accountId: UUID): Subscribe?
}
