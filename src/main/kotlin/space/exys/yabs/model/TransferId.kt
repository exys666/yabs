package space.exys.yabs.model

import java.io.Serializable
import java.util.*

data class TransferId(
        private val value: UUID
) : Serializable {

    companion object {

        fun random(): TransferId = TransferId(UUID.randomUUID())
    }

    constructor(value: String) : this(UUID.fromString(value))

    override fun toString() = value.toString()

}