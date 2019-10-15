package space.exys.yabs.model

import java.math.BigDecimal
import java.time.Instant

data class BalanceChange(

        val accountId: AccountId,
        val amount: BigDecimal,
        val createdAt: Instant = Instant.now(),
        val id: BalanceChangeId = BalanceChangeId.random()
)