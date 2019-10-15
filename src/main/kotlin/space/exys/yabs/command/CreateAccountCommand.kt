package space.exys.yabs.command

import space.exys.yabs.db.Database
import space.exys.yabs.model.Account
import space.exys.yabs.model.BalanceChange
import space.exys.yabs.persistance.AccountsRepository
import space.exys.yabs.persistance.BalanceChangesRepository
import java.math.BigDecimal
import javax.inject.Inject

class CreateAccountCommand @Inject constructor(
        private val db: Database,
        private val accountsRepository: AccountsRepository,
        private val balanceChangesRepository: BalanceChangesRepository
) {

    fun execute(firstName: String, lastName: String): Account {
        val account = Account(ownerFirstName = firstName, ownerLastName = lastName)

        db.connection.use {
            accountsRepository.save(it, account)
            balanceChangesRepository.save(it, BalanceChange(account.id, BigDecimal("100.00")))
            it.commit()
        }

        return account
    }
}