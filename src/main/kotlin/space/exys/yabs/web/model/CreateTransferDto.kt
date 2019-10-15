package space.exys.yabs.web.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import space.exys.yabs.model.AccountId
import java.math.BigDecimal

data class CreateTransferDto(
        @JsonSerialize(using = AccountId.Serializer::class)
        @JsonDeserialize(using = AccountId.Deserializer::class)
        val accountId: AccountId,
        val amount: BigDecimal
)