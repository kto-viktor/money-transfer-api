package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Account

interface CallbackService {
    fun sendCallback(account: Account)
}
