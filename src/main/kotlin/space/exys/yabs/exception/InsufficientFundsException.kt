package space.exys.yabs.exception

import space.exys.yabs.model.AccountId
import java.math.BigDecimal

data class InsufficientFundsException(
        val accountId: AccountId,
        val balance: BigDecimal,
        val requestedAmount: BigDecimal
) : RuntimeException("Account [$accountId] has insufficient fund ($balance) for transfer $requestedAmount")