package space.exys.yabs.command

import space.exys.yabs.config.Database
import space.exys.yabs.model.Account
import space.exys.yabs.persistance.AccountsRepository

object CreateAccountCommand {

    fun execute(firstName: String, lastName: String): Account {
        val account = Account(ownerFirstName = firstName, ownerLastName = lastName)

        Database.connection.use {
            AccountsRepository.save(it, account)
            it.commit()
        }

        return account
    }
}