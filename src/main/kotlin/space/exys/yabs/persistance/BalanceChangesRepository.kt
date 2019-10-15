package space.exys.yabs.persistance

import space.exys.yabs.model.AccountId
import space.exys.yabs.model.BalanceChange
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Timestamp

open class BalanceChangesRepository {

    open fun save(connection: Connection, change: BalanceChange): BalanceChange {
        connection.prepareStatement("""
            INSERT INTO balance_change (id, account_id, amount, created_at)
            VALUES (?, ?, ?, ?)
        """.trimIndent()).apply {
            setString(1, change.id.toString())
            setString(2, change.accountId.toString())
            setBigDecimal(3, change.amount)
            setTimestamp(4, Timestamp.from(change.createdAt))
        }.executeUpdate()
        return change;
    }

    open fun getBalance(connection: Connection, accountId: AccountId): BigDecimal {
        val results = connection.prepareStatement("""
            SELECT SUM(amount) AS balance
            FROM balance_change
            WHERE account_id = ?
        """.trimIndent()).apply {
            setString(1, accountId.toString())
        }.executeQuery()

        if (!results.next()) {
            return BigDecimal.ZERO
        }

        return results.getBigDecimal("balance") ?: BigDecimal.ZERO
    }

}