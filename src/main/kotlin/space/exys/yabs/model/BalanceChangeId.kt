package space.exys.yabs.model

import java.io.Serializable
import java.util.*

data class BalanceChangeId(
        private val value: UUID
) : Serializable {

    companion object {

        fun random(): BalanceChangeId = BalanceChangeId(UUID.randomUUID())
    }

    constructor(value: String) : this(UUID.fromString(value))

    override fun toString() = value.toString()

}