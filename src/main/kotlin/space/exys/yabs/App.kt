package space.exys.yabs

import io.javalin.Javalin
import io.javalin.core.validation.JavalinValidation
import org.codejargon.feather.Feather
import space.exys.yabs.db.InMemoryH2Database
import space.exys.yabs.model.AccountId
import space.exys.yabs.web.AccountsController

fun main() {
    val app = App()
    app.start()
    println("App url http://localhost:${app.port()}")
}

class App(
        private val javalin: Javalin = Javalin.create(),
        private val feather: Feather = Feather.with(InMemoryH2Database("live"))
) {

    init {
        JavalinValidation.register(AccountId::class.java) { v -> AccountId(v) }

        val accountsController = feather.instance(AccountsController::class.java)

        javalin.get("/accounts/:accountId", accountsController::get)
        javalin.post("/accounts", accountsController::create)
        javalin.post("/accounts/:accountId/transfers", accountsController::createTransfer)
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
