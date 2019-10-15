package space.exys.yabs.command

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import space.exys.yabs.db.Database
import space.exys.yabs.model.Account
import space.exys.yabs.model.BalanceChange
import space.exys.yabs.persistance.AccountsRepository
import space.exys.yabs.persistance.BalanceChangesRepository
import java.math.BigDecimal
import java.sql.Connection


@ExtendWith(MockitoExtension::class)
@DisplayName("CreateAccountCommand")
class CreateAccountCommandTest {

    @Mock
    lateinit var connection: Connection

    @Mock
    lateinit var db: Database

    @Mock
    lateinit var accountRepository: AccountsRepository

    @Mock
    lateinit var balanceChangesRepository: BalanceChangesRepository

    @InjectMocks
    lateinit var command: CreateAccountCommand

    @BeforeEach
    fun init() {
        lenient().`when`(db.connection).thenReturn(connection)
    }

    @Test
    fun `should create account with initial bonus`() {
        // given

        // when
        command.execute("Joe", "Doe")

        // then
        val account = argumentCaptor<Account>()
        verify(accountRepository).save(eq(connection), account.capture())
        assertThat(account.firstValue.ownerFirstName).isEqualTo("Joe")
        assertThat(account.firstValue.ownerLastName).isEqualTo("Doe")

        val change = argumentCaptor<BalanceChange>()
        verify(balanceChangesRepository).save(eq(connection), change.capture())
        assertThat(change.firstValue.accountId).isEqualTo(account.firstValue.id)
        assertThat(change.firstValue.amount).isEqualTo(BigDecimal("100.00"))

    }
}