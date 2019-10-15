package space.exys.yabs.persistance

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import space.exys.yabs.db.InMemoryH2Database
import space.exys.yabs.model.Account
import space.exys.yabs.model.BalanceChange
import space.exys.yabs.model.Transfer
import java.math.BigDecimal
import java.sql.Connection


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("TransfersRepository")
class TransfersRepositoryTest {

    private val db = InMemoryH2Database("transfers")
    private val accountsRepository = AccountsRepository()
    private val balanceChangeRepository = BalanceChangesRepository()
    private val repository = TransfersRepository()
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
    fun `should save transfer`() {
        // given
        val debtorAccount = accountsRepository.save(connection, Account("Joe", "Doe"))
        val creditorAccount = accountsRepository.save(connection, Account("Joe", "Doe"))

        val debtChange = balanceChangeRepository.save(connection, BalanceChange(debtorAccount.id, BigDecimal("-6.67")))
        val creditChange = balanceChangeRepository.save(connection, BalanceChange(creditorAccount.id, BigDecimal("6.67")))

        val transfer = Transfer(debtChange, creditChange)

        // when
        repository.save(connection, transfer)

        // then
        val result = connection.prepareStatement("""
            SELECT * FROM transfer WHERE id = ?
        """.trimIndent()).apply {
            setString(1, transfer.id.toString())
        }.executeQuery()

        assertThat(result.next()).isTrue()
        assertThat(result.getString("id")).isEqualTo(transfer.id.toString())
        assertThat(result.getString("debt_change_id")).isEqualTo(debtChange.id.toString())
        assertThat(result.getString("credit_change_id")).isEqualTo(creditChange.id.toString())
        assertThat(result.next()).isFalse()
    }

}