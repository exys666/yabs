package space.exys.yabs.query

import space.exys.yabs.db.Database
import space.exys.yabs.model.AccountId
import space.exys.yabs.persistance.AccountsRepository
import space.exys.yabs.persistance.BalanceChangesRepository
import space.exys.yabs.web.model.AccountDto
import space.exys.yabs.web.model.OwnerDto
import java.math.BigDecimal
import javax.inject.Inject

class GetAccountDetailsQuery @Inject constructor(
        private val db: Database,
        private val accountsRepository: AccountsRepository,
        private val balanceChangesRepository: BalanceChangesRepository
) {

    fun execute(id: AccountId): AccountDto? =
            db.connection.use { connection ->
                accountsRepository.findById(connection, id)
                        ?.let { account ->
                            AccountDto(
                                    id = account.id,
                                    owner = OwnerDto(account.ownerFirstName, account.ownerLastName),
                                    balance = balanceChangesRepository.getBalance(connection, id)
                            )
                        }
            }

}