package space.exys.yabs.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object Database {

    private val dataSource = HikariConfig()
            .apply {
                jdbcUrl = "jdbc:h2:mem:default"
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
    }

    val connection: Connection
        get() = dataSource.connection.apply {
            autoCommit = false
        }
}