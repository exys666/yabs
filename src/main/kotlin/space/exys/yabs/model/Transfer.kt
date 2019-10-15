package space.exys.yabs.model

data class Transfer(

        val debtChangeId: BalanceChangeId,
        val creditChangeId: BalanceChangeId,
        val id: TransferId = TransferId.random()
) {
    constructor(debtorChange: BalanceChange, creditorChange: BalanceChange)
            : this(debtorChange.id, creditorChange.id)
}