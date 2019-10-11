package space.exys.yabs.web

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import space.exys.yabs.command.CreateAccountCommand
import space.exys.yabs.model.AccountId
import space.exys.yabs.query.GetAccountDetailsQuery
import space.exys.yabs.web.model.AccountDto
import space.exys.yabs.web.model.CreateAccountDto
import space.exys.yabs.web.model.OwnerDto
import java.lang.Exception
import java.math.BigDecimal
import java.util.*

class AccountsController {

    fun create(ctx: Context) {
        val dto = ctx.bodyValidator<CreateAccountDto>()
                .check({ it.owner.firstName.trim().isNotEmpty() }, "First name has to be not empty")
                .check({ it.owner.lastName.trim().isNotEmpty() }, "Last name has to be not empty")
                .get()

        val account = CreateAccountCommand.execute(dto.owner.firstName, dto.owner.lastName)

        ctx.status(201)
        ctx.json(AccountDto(
                id = account.id,
                owner = OwnerDto(account.ownerFirstName, account.ownerLastName),
                balance = BigDecimal("0.00")))
    }

    fun get(ctx: Context) {
        try {
            val accountId = ctx.pathParam<AccountId>("accountId").getOrNull() ?: throw NotFoundResponse()

            val account = GetAccountDetailsQuery.execute(accountId) ?: throw NotFoundResponse()
            ctx.json(account)
        } catch (ex: BadRequestResponse) {
            throw NotFoundResponse()
        }
    }



}