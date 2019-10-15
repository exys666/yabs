package space.exys.yabs.command

import io.javalin.http.NotFoundResponse
import space.exys.yabs.db.Database
import space.exys.yabs.exception.CreditorAccountNotFoundException
import space.exys.yabs.exception.DebtorAccountNotFoundException
import space.exys.yabs.exception.InsufficientFundsException
import space.exys.yabs.exception.InvalidTransferAmountException
import space.exys.yabs.model.AccountId
import space.exys.yabs.model.BalanceChange
import space.exys.yabs.model.Transfer
import space.exys.yabs.persistance.AccountsRepository
import space.exys.yabs.persistance.BalanceChangesRepository
import space.exys.yabs.persistance.TransfersRepository
import java.math.BigDecimal
import javax.inject.Inject

class CreateTransferCommand @Inject constructor(
        private val db: Database,
        private val accountsRepository: AccountsRepository,
        private val balanceChangesRepository: BalanceChangesRepository,
        private val transfersRepository: TransfersRepository
) {

    fun execute(debtorAccountId: AccountId, creditorAccountId: AccountId, amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) {
            throw InvalidTransferAmountException(amount)
        }

        db.connection.use { connection ->
            accountsRepository.findByIdAndLock(connection, debtorAccountId)
                    ?: throw DebtorAccountNotFoundException(debtorAccountId)

            accountsRepository.findById(connection, creditorAccountId)
                    ?: throw CreditorAccountNotFoundException(creditorAccountId)

            val balance = balanceChangesRepository.getBalance(connection, debtorAccountId) ?: throw NotFoundResponse()
            if (balance < amount) {
                throw InsufficientFundsException(debtorAccountId, balance, amount)
            }

            val debtorChange = BalanceChange(debtorAccountId, amount.negate())
            val creditorChange = BalanceChange(creditorAccountId, amount)

            balanceChangesRepository.save(connection, debtorChange)
            balanceChangesRepository.save(connection, creditorChange)

            transfersRepository.save(connection, Transfer(debtorChange, creditorChange))

            connection.commit()
        }
    }
}