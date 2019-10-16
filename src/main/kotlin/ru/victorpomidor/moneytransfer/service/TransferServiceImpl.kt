package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Account
import ru.victorpomidor.moneytransfer.model.ChangeBalanceResult
import ru.victorpomidor.moneytransfer.model.ChangeBalanceResult.NOT_ENOUGH_BALANCE
import ru.victorpomidor.moneytransfer.model.RequestKey
import ru.victorpomidor.moneytransfer.model.SaveResult
import ru.victorpomidor.moneytransfer.model.SaveResult.Duplicated
import ru.victorpomidor.moneytransfer.model.Transfer
import ru.victorpomidor.moneytransfer.model.TransferStatus
import ru.victorpomidor.moneytransfer.model.TransferStatusCode
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.CURRENCIES_NOT_MATCH
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.INSUFFICIENT_FUNDS
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.IN_PROGRESS
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.SOURCE_ACCOUNT_NOT_FOUND
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.SUCCESS
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.TARGET_ACCOUNT_NOT_FOUND
import ru.victorpomidor.moneytransfer.repository.KeyValueRepository

class TransferServiceImpl(
    private val accountService: AccountService,
    private val repository: KeyValueRepository<RequestKey, Transfer>
) : TransferService {

    override fun executeTransfer(transfer: Transfer): Transfer {
        val saveResult = createTransferInProgress(transfer)
        if (saveResult is Duplicated) return saveResult.value
        val executedTransfer = checkAndExecuteTransfer(transfer)
        repository.update(transfer.requestKey(), executedTransfer)
        return executedTransfer
    }

    private fun createTransferInProgress(transfer: Transfer): SaveResult<Transfer> {
        return repository.add(transfer.requestKey(), transferWithStatus(transfer, IN_PROGRESS))
    }

    private fun checkAndExecuteTransfer(transfer: Transfer): Transfer {
        val sourceAccount = accountService.getAccount(transfer.sourceAccountId)
            ?: return transferWithStatus(transfer, SOURCE_ACCOUNT_NOT_FOUND)
        val targetAccount = accountService.getAccount(transfer.targetAccountId)
            ?: return transferWithStatus(transfer, TARGET_ACCOUNT_NOT_FOUND)
        return when {
            currenciesNotMatch(sourceAccount, targetAccount) -> transferWithStatus(transfer, CURRENCIES_NOT_MATCH)
            trySubtract(sourceAccount, transfer) == NOT_ENOUGH_BALANCE ->
                transferWithStatus(transfer, INSUFFICIENT_FUNDS)
            else -> {
                accountService.addBalance(targetAccount.id, transfer.amount)
                transferWithStatus(transfer, SUCCESS)
            }
        }
    }

    private fun trySubtract(
        sourceAccount: Account,
        transfer: Transfer
    ): ChangeBalanceResult {
        return accountService.subtractBalance(sourceAccount.id, transfer.amount)
    }

    private fun currenciesNotMatch(
        sourceAccount: Account,
        targetAccount: Account
    ): Boolean {
        return sourceAccount.currency != targetAccount.currency
    }

    private fun transferWithStatus(transfer: Transfer, code: TransferStatusCode): Transfer {
        return transfer.apply {
            status = TransferStatus(code, getErrorMessageFor(transfer, code))
        }
    }

    private fun getErrorMessageFor(transfer: Transfer, code: TransferStatusCode): String {
        return when (code) {
            SUCCESS, IN_PROGRESS -> ""
            INSUFFICIENT_FUNDS -> "Not enough balance on account ${transfer.sourceAccountId}"
            SOURCE_ACCOUNT_NOT_FOUND -> "Source account with id ${transfer.sourceAccountId} not found."
            TARGET_ACCOUNT_NOT_FOUND -> "Target account with id ${transfer.targetAccountId} not found."
            CURRENCIES_NOT_MATCH -> "Currencies of accounts not match."
        }
    }
}
