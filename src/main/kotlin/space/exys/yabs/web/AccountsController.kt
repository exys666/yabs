package space.exys.yabs.web

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import space.exys.yabs.command.CreateAccountCommand
import space.exys.yabs.command.CreateTransferCommand
import space.exys.yabs.exception.AccountNotFoundException
import space.exys.yabs.exception.InsufficientFundsException
import space.exys.yabs.exception.InvalidTransferAmountException
import space.exys.yabs.model.AccountId
import space.exys.yabs.query.GetAccountDetailsQuery
import space.exys.yabs.web.model.AccountDto
import space.exys.yabs.web.model.CreateAccountDto
import space.exys.yabs.web.model.CreateTransferDto
import space.exys.yabs.web.model.OwnerDto
import java.lang.Exception
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class AccountsController @Inject constructor(
        private val createAccountCommand: CreateAccountCommand,
        private val getAccountDetailsQuery: GetAccountDetailsQuery,
        private val createTransferCommand: CreateTransferCommand
) {

    fun create(ctx: Context) {
        val dto = ctx.bodyValidator<CreateAccountDto>()
                .check({ it.owner.firstName.trim().isNotEmpty() }, "First name has to be not empty")
                .check({ it.owner.lastName.trim().isNotEmpty() }, "Last name has to be not empty")
                .get()

        val account = createAccountCommand.execute(dto.owner.firstName, dto.owner.lastName)

        ctx.status(201)
        ctx.json(AccountDto(
                id = account.id,
                owner = OwnerDto(account.ownerFirstName, account.ownerLastName),
                balance = BigDecimal("0.00")))
    }

    fun get(ctx: Context) {
            val accountId = getAccountId(ctx)
            val account = getAccountDetailsQuery.execute(accountId) ?: throw NotFoundResponse()
            ctx.json(account)
    }

    fun createTransfer(ctx: Context) {
        try {
            val accountId = getAccountId(ctx)
            val dto = ctx.body<CreateTransferDto>()

            createTransferCommand.execute(accountId, dto.accountId, dto.amount)
            ctx.status(201)
        } catch (ex: InvalidTransferAmountException) {
            ctx.status(400)
        } catch (ex: InsufficientFundsException) {
            ctx.status(412)
        } catch (ex: AccountNotFoundException) {
            ctx.status(404)
        }
    }

    private fun getAccountId(ctx: Context): AccountId = try {
        ctx.pathParam<AccountId>("accountId").getOrNull() ?: throw NotFoundResponse()
    } catch (ex: BadRequestResponse) {
        // map wrong accountId to not found
        throw NotFoundResponse()
    }

}