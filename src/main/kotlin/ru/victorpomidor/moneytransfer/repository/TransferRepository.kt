package ru.victorpomidor.moneytransfer.repository

import ru.victorpomidor.moneytransfer.model.RequestKey
import ru.victorpomidor.moneytransfer.model.Transfer

class TransferRepository : ConcurrentMapKeyValueRepository<RequestKey, Transfer>() {
    override fun update(key: RequestKey, value: Transfer) {
        val oldValue = storageMap[key] ?: throw IllegalArgumentException("transfer not found")
        val newValue = oldValue.copy(status = value.status)
        storageMap[key] = newValue
    }
}
