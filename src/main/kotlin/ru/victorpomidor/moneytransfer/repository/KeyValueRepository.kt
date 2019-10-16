package ru.victorpomidor.moneytransfer.repository

import ru.victorpomidor.moneytransfer.model.SaveResult

/**
 * Repository abstraction for storing object in key-value storage */
interface KeyValueRepository<K, V> {
    /**
     * Return a object
     * @param key key for value
     * @return a value object if founded, or null if isn't
     */
    fun get(key: K): V?

    /**
     * Add a new value to repository
     * @param value new value
     * @return SaveResult.Success if success, or SaveResult.Duplicated with an already existed value if duplicated,
     * new value will be ignored in that case
     */
    fun add(key: K, value: V): SaveResult<V>

    /**
     * Update a value
     * @throws IllegalArgumentException if key doesn't exist
     */
    fun update(key: K, value: V)
}
