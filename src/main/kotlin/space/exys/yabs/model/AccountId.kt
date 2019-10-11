package space.exys.yabs.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.Serializable
import java.util.*

data class AccountId(
        private val value: UUID
) : Serializable {

    companion object {

        fun random(): AccountId = AccountId(UUID.randomUUID())
    }

    constructor(value: String) : this(UUID.fromString(value))

    override fun toString() = value.toString()

    class JsonSerializer : com.fasterxml.jackson.databind.JsonSerializer<AccountId>() {

        override fun serialize(id: AccountId, genertor: JsonGenerator, provider: SerializerProvider) {
            genertor.writeString(id.toString())
        }
    }

}