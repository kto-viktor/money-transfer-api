package ru.victorpomidor.moneytransfer.model

sealed class SaveResult<T>(val value: T) {
    class Success<T>(value: T) : SaveResult<T>(value)
    class Duplicated<T>(currentValue: T) : SaveResult<T>(currentValue)
}
