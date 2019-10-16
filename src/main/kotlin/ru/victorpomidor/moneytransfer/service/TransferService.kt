package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Transfer

interface TransferService {
    fun executeTransfer(transfer: Transfer): Transfer
}
