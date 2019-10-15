package space.exys.yabs.exception

import java.math.BigDecimal

data class InvalidTransferAmountException(
        val amount: BigDecimal
) : RuntimeException("Invalid transer amount $amount")