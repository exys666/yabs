package space.exys.yabs.persistance

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.h2.jdbc.JdbcSQLTimeoutException
import org.junit.jupiter.api.*
import space.exys.yabs.db.InMemoryH2Database
import space.exys.yabs.model.Account
import java.sql.Connection


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AccountsRepository")
class AccountsRepositoryTest {

    private val db = InMemoryH2Database("accounts")
    private val repository = AccountsRepository()
    private lateinit var connection: Connection
    private lateinit var connection2: Connection

    @BeforeEach
    fun init() {
        connection = db.connection
        connection2 = db.connection
    }

    @AfterEach
    fun rollback() {
        connection.rollback()
    }

    @Test
    fun `should save and find Account`() {
        // given
        val account = Account(
                ownerFirstName = "Joe",
                ownerLastName = "Doe"
        )

        // when
        repository.save(connection, account)

        // then
        val found = repository.findById(connection, account.id)
        assertThat(found).isEqualTo(account)
    }

    @Test
    fun `should lock Account for update`() {
        // given
        val account = repository.save(connection, Account("Joe", "Doe"))
        connection.commit()

        // when
        repository.findByIdAndLock(connection, account.id)

        // then
        assertThatThrownBy {
            connection2.prepareStatement("""
                SELECT * FROM account WHERE id = ? FOR UPDATE
            """).apply {
                setString(1, account.id.toString())
            }.executeQuery()
        }.isInstanceOf(JdbcSQLTimeoutException::class.java)

        connection.prepareStatement("""
            DELETE FROM account WHERE id = ?
        """.trimIndent()).apply {
            setString(1, account.id.toString())
        }.executeUpdate()

        connection.commit()
    }
}