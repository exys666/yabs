package space.exys.yabs.persistance

import org.junit.jupiter.api.*
import space.exys.yabs.config.Database
import space.exys.yabs.model.Account
import java.sql.Connection

@DisplayName("AccountsRepository")
class AccountsRepositoryTest {

    private lateinit var connection: Connection

    @BeforeEach
    fun init() {
        connection = Database.connection
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
        AccountsRepository.save(connection, account)

        // then
        val found = AccountsRepository.findById(connection, account.id)
        Assertions.assertEquals(found, account) // TODO assertJ
    }
}