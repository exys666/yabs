package space.exys.yabs.query

import space.exys.yabs.config.Database
import space.exys.yabs.model.AccountId
import space.exys.yabs.persistance.AccountsRepository
import space.exys.yabs.web.model.AccountDto
import space.exys.yabs.web.model.OwnerDto
import java.math.BigDecimal

object GetAccountDetailsQuery {

    fun execute(id: AccountId): AccountDto? =
            Database.connection.use { con ->
                AccountsRepository.findById(con, id)
                        ?.let { account ->
                            AccountDto(
                                    id = account.id,
                                    owner = OwnerDto(account.ownerFirstName, account.ownerLastName),
                                    balance = BigDecimal("0.00")
                            )
                        }
            }

}