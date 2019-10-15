package space.exys.yabs.command

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import space.exys.yabs.db.Database
import space.exys.yabs.exception.CreditorAccountNotFoundException
import space.exys.yabs.exception.DebtorAccountNotFoundException
import space.exys.yabs.exception.InsufficientFundsException
import space.exys.yabs.exception.InvalidTransferAmountException
import space.exys.yabs.model.Account
import space.exys.yabs.model.AccountId
import space.exys.yabs.model.BalanceChange
import space.exys.yabs.model.Transfer
import space.exys.yabs.persistance.AccountsRepository
import space.exys.yabs.persistance.BalanceChangesRepository
import space.exys.yabs.persistance.TransfersRepository
import java.math.BigDecimal
import java.sql.Connection

@ExtendWith(MockitoExtension::class)
@DisplayName("CreateTransferCommand")
class CreateTransferCommandTest {

    @Mock
    lateinit var connection: Connection

    @Mock
    lateinit var db: Database

    @Mock
    lateinit var accountRepository: AccountsRepository

    @Mock
    lateinit var balanceChangesRepository: BalanceChangesRepository

    @Mock
    lateinit var transfersRepository: TransfersRepository

    @InjectMocks
    lateinit var commandTest: CreateTransferCommand

    @BeforeEach
    fun init() {
        lenient().`when`(db.connection).thenReturn(connection)
    }


    @Test
    fun `should throw AccountNotFoundException if debtor account not found`() {
        // given
        val debtorId = AccountId.random()

        assertThatThrownBy {
            // when
            commandTest.execute(debtorId, AccountId.random(), BigDecimal("10.00"))
        }
                // then
                .isEqualTo(DebtorAccountNotFoundException(debtorId))
    }

    @Test
    fun `should throw AccountNotFoundException if creditor account not found`() {
        // given
        val debtorId = AccountId.random()
        given(accountRepository.findByIdAndLock(connection, debtorId)).willReturn(Account("Joe", "Doe", debtorId))

        val creditorId = AccountId.random()

        assertThatThrownBy {
            // when
            commandTest.execute(debtorId, creditorId, BigDecimal("10.00"))
        }
                // then
                .isEqualTo(CreditorAccountNotFoundException(creditorId))
    }

    @Test
    fun `should throw InsufficientFundsException if transfer amount greater then account balance`() {
        // given
        val debtorId = AccountId.random()
        given(accountRepository.findByIdAndLock(connection, debtorId)).willReturn(Account("Joe", "Doe", debtorId))
        given((balanceChangesRepository.getBalance(connection, debtorId))).willReturn(BigDecimal("1.00"))

        val creditorId = AccountId.random()
        given(accountRepository.findById(connection, creditorId)).willReturn(Account("Bob", "Doe", creditorId))

        assertThatThrownBy {
            // when
            commandTest.execute(debtorId, creditorId, BigDecimal("10.00"))
        }
                // then
                .isEqualTo(InsufficientFundsException(debtorId, BigDecimal("1.00"), BigDecimal("10.00")))

    }

    @Test
    fun `should throw InvalidTransferAmountException if transfer amount not positive`() {
        // given

        assertThatThrownBy {
            // when
            commandTest.execute(AccountId.random(), AccountId.random(), BigDecimal("0.00"))
        }
                // then
                .isEqualTo(InvalidTransferAmountException(BigDecimal("0.00")))

    }

    @Test
    fun `should make transfer`() {
        // given
        val debtorId = AccountId.random()
        given(accountRepository.findByIdAndLock(connection, debtorId)).willReturn(Account("Joe", "Doe", debtorId))
        given((balanceChangesRepository.getBalance(connection, debtorId))).willReturn(BigDecimal("100.00"))

        val creditorId = AccountId.random()
        given(accountRepository.findById(connection, creditorId)).willReturn(Account("Bob", "Doe", creditorId))

        // when
        commandTest.execute(debtorId, creditorId, BigDecimal("10.00"))


        // then
        val change = argumentCaptor<BalanceChange>()
        verify(balanceChangesRepository, times(2)).save(eq(connection), change.capture())
        assertThat(change.firstValue.accountId).isEqualTo(debtorId)
        assertThat(change.secondValue.accountId).isEqualTo(creditorId)
        assertThat(change.firstValue.amount).isEqualTo(BigDecimal("-10.00"))
        assertThat(change.secondValue.amount).isEqualTo(BigDecimal("10.00"))

        val transfer = argumentCaptor<Transfer>()
        verify(transfersRepository).save(eq(connection), transfer.capture())
        assertThat(transfer.firstValue.debtChangeId).isEqualTo(change.firstValue.id)
        assertThat(transfer.firstValue.creditChangeId).isEqualTo(change.secondValue.id)

        verify(connection).commit()
    }

}