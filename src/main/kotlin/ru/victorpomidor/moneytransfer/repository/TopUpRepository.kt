package ru.victorpomidor.moneytransfer.repository

import ru.victorpomidor.moneytransfer.model.RequestKey
import ru.victorpomidor.moneytransfer.model.TopUp

class TopUpRepository : ConcurrentMapKeyValueRepository<RequestKey, TopUp>() {
    override fun update(key: RequestKey, value: TopUp) {
        val oldValue = storageMap[key] ?: throw IllegalArgumentException("topup not found")
        val newValue = oldValue.copy(status = value.status)
        storageMap[key] = newValue
    }
}
