package space.exys.yabs.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.codejargon.feather.Provides
import java.sql.Connection
import javax.inject.Singleton

class InMemoryH2Database(
        val name: String
) : Database {

    private val dataSource = HikariConfig()
            .apply {
                jdbcUrl = "jdbc:h2:mem:$name"
                driverClassName = "org.h2.Driver"
            }
            .let { HikariDataSource(it) }

    init {
        connection.prepareStatement("""
            CREATE TABLE account (
                id                  UUID    NOT NULL,
                owner_first_name    VARCHAR NOT NULL,
                owner_last_name     VARCHAR NOT NULL,

                PRIMARY KEY (id)
            )
        """.trimIndent()).executeUpdate()

        connection.prepareStatement("""
            CREATE TABLE balance_change (
                id          UUID        NOT NULL,
                account_id  UUID        NOT NULL,
                amount      DECIMAL     NOT NULL,
                created_at  TIMESTAMP   NOT NULL,
                
                PRIMARY KEY (id),
                FOREIGN KEY (account_id) REFERENCES account (id)
            )
        """.trimIndent()).executeUpdate()

        connection.prepareStatement("""
            CREATE TABLE transfer (
                id                  UUID    NOT NULL,
                debt_change_id    UUID    NOT NULL,
                credit_change_id  UUID    NOT NULL,
                
                PRIMARY KEY (id),
                FOREIGN KEY (debt_change_id)   REFERENCES balance_change (id),
                FOREIGN KEY (credit_change_id) REFERENCES balance_change (id)
            )
        """.trimIndent()).executeUpdate()
    }

    override val connection: Connection
        get() = dataSource.connection.apply {
            autoCommit = false
        }

    @Provides
    @Singleton
    fun instance(): Database = this
}