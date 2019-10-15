package space.exys.yabs.persistance

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import space.exys.yabs.db.InMemoryH2Database
import space.exys.yabs.model.Account
import space.exys.yabs.model.BalanceChange
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Timestamp


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BalanceChangesRepositoryTest {

    private val db = InMemoryH2Database("changes")
    private val accountsRepository = AccountsRepository()
    private val repository = BalanceChangesRepository()
    private lateinit var connection: Connection

    @BeforeEach
    fun init() {
        connection = db.connection
    }

    @AfterEach
    fun rollback() {
        connection.rollback()
    }

    @Test
    fun `should save change`() {
        // given
        val account = accountsRepository.save(connection, Account("Joe", "Doe"))
        val change = BalanceChange(account.id, BigDecimal("6.67"))

        // when
        repository.save(connection, change)

        // then
        val result = connection.prepareStatement("""
            SELECT * FROM balance_change WHERE id = ?
        """.trimIndent()).apply {
            setString(1, change.id.toString())
        }.executeQuery()

        assertThat(result.next()).isTrue()
        assertThat(result.getString("id")).isEqualTo(change.id.toString())
        assertThat(result.getString("account_id")).isEqualTo(account.id.toString())
        assertThat(result.getBigDecimal("amount")).isEqualTo(change.amount)
        assertThat(result.getTimestamp("created_at")).isEqualTo(Timestamp.from(change.createdAt))
        assertThat(result.next()).isFalse()
    }

    @Test
    fun `getBalance() should return 0 for empty change list`() {
        // given
        val account = accountsRepository.save(connection, Account("Joe", "Doe"))

        // when
        val balance = repository.getBalance(connection, account.id)

        // then
        assertThat(balance).isEqualByComparingTo("0.00")
    }

    @Test
    fun `getBalance() should return sum of changes`() {
        // given
        val account = accountsRepository.save(connection, Account("Joe", "Doe"))

        repository.save(connection, BalanceChange(account.id, BigDecimal("100.00")))
        repository.save(connection, BalanceChange(account.id, BigDecimal("-1.01")))
        repository.save(connection, BalanceChange(account.id, BigDecimal("1.00")))

        // when
        val balance = repository.getBalance(connection, account.id)

        // then
        assertThat(balance).isEqualByComparingTo("99.99")
    }
}