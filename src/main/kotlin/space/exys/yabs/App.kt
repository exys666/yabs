package space.exys.yabs

import io.javalin.Javalin
import io.javalin.core.validation.JavalinValidation
import space.exys.yabs.model.AccountId
import space.exys.yabs.web.AccountsController

fun main() {
    App().start()
}

class App(
        private val javalin: Javalin = Javalin.create()
) {

    init {
        JavalinValidation.register(AccountId::class.java) { v -> AccountId(v) }

        val accountsController = AccountsController()

        javalin.get("/accounts/:accountId", accountsController::get)
        javalin.post("/accounts", accountsController::create)

    }

    fun start() {
        javalin.start();
    }

    fun stop() {
        javalin.stop()
    }

    fun port(): Int {
        return javalin.port();
    }
}
