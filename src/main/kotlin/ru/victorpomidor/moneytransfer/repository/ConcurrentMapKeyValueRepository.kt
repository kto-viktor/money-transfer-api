package ru.victorpomidor.moneytransfer.repository

import ru.victorpomidor.moneytransfer.model.SaveResult
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Implementation of KeyValueRepository based on ConcurrentMap. Update method still abstract and concrete
 * realisations should implement it, because different entities requires different fields and logic to update
 */
abstract class ConcurrentMapKeyValueRepository<K, V> : KeyValueRepository<K, V> {
    protected val storageMap: ConcurrentMap<K, V> = ConcurrentHashMap()

    override fun get(key: K): V? {
        return storageMap[key]
    }

    override fun add(key: K, value: V): SaveResult<V> {
        val putResult = storageMap.putIfAbsent(key, value)
        return if (putResult == null) {
            SaveResult.Success(value)
        } else {
            SaveResult.Duplicated(putResult)
        }
    }
}
