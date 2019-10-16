package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.RequestKey
import ru.victorpomidor.moneytransfer.model.SaveResult
import ru.victorpomidor.moneytransfer.model.SaveResult.Duplicated
import ru.victorpomidor.moneytransfer.model.SaveResult.Success
import ru.victorpomidor.moneytransfer.model.TopUp
import ru.victorpomidor.moneytransfer.model.TopUpStatus
import ru.victorpomidor.moneytransfer.model.TopUpStatus.ACCOUNT_NOT_FOUND
import ru.victorpomidor.moneytransfer.model.TopUpStatus.IN_PROGRESS
import ru.victorpomidor.moneytransfer.model.TopUpStatus.SUCCESS
import ru.victorpomidor.moneytransfer.repository.KeyValueRepository
import java.util.UUID

class TopUpServiceImpl(
    private val accountService: AccountService,
    private val repository: KeyValueRepository<RequestKey, TopUp>
) : TopUpService {
    override fun executeTopUp(accountId: UUID, topUp: TopUp): TopUp {
        if (accountService.getAccount(accountId) == null) {
            return topUpWithStatus(topUp, ACCOUNT_NOT_FOUND)
        }
        return when (val saveResult = createTopUpInProgress(topUp)) {
            is Duplicated -> saveResult.value
            is Success -> {
                accountService.addBalance(accountId, topUp.amount)
                val successTopUp = topUpWithStatus(topUp, SUCCESS)
                repository.update(topUp.requestKey(), successTopUp)
                successTopUp
            }
        }
    }

    private fun createTopUpInProgress(topUp: TopUp): SaveResult<TopUp> {
        return repository.add(topUp.requestKey(), topUpWithStatus(topUp, IN_PROGRESS))
    }

    private fun topUpWithStatus(topUp: TopUp, status: TopUpStatus): TopUp {
        return topUp.copy(status = status)
    }
}
