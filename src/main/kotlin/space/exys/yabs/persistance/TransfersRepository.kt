package space.exys.yabs.persistance

import space.exys.yabs.model.Transfer
import java.sql.Connection

open class TransfersRepository {

    open fun save(connection: Connection, transfer: Transfer): Transfer {
        connection.prepareStatement("""
            INSERT INTO transfer (id, debt_change_id, credit_change_id)
            VALUES (?, ?, ?)
        """.trimIndent()).apply {
            setString(1, transfer.id.toString())
            setString(2, transfer.debtChangeId.toString())
            setString(3, transfer.creditChangeId.toString())
        }.executeUpdate()
        return transfer
    }
}