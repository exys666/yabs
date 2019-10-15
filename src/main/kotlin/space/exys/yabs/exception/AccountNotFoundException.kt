package space.exys.yabs.exception

import space.exys.yabs.model.AccountId

abstract class AccountNotFoundException(
        accountId: AccountId
) : RuntimeException("Account [$accountId] not found")

data class DebtorAccountNotFoundException(
        val accountId: AccountId
) : AccountNotFoundException(accountId)

data class CreditorAccountNotFoundException(
        val accountId: AccountId
) : AccountNotFoundException(accountId)
