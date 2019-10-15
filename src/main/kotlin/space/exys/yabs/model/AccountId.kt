package space.exys.yabs.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
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

    class Serializer : JsonSerializer<AccountId>() {

        override fun serialize(id: AccountId, genertor: JsonGenerator, provider: SerializerProvider) {
            genertor.writeString(id.toString())
        }
    }

    class Deserializer : JsonDeserializer<AccountId>() {

        override fun deserialize(parser: JsonParser, ctx: DeserializationContext) = AccountId(parser.valueAsString)
    }

}