package space.exys.yabs.persistance

import space.exys.yabs.model.Account
import space.exys.yabs.model.AccountId
import java.sql.Connection

// TODO remove open
open class AccountsRepository {

    open fun save(connection: Connection, account: Account): Account {
        connection.prepareStatement("""
            INSERT INTO account (id, owner_first_name, owner_last_name)
            VALUES (?, ?, ?)
        """.trimIndent()).apply {
            setString(1, account.id.toString())
            setString(2, account.ownerFirstName)
            setString(3, account.ownerLastName)
        }.executeUpdate()
        return account
    }

    open fun findById(connection: Connection, id: AccountId): Account? {
        val results = connection.prepareStatement("""
            SELECT owner_first_name, owner_last_name
            FROM account
            WHERE id = ?
        """.trimIndent()).apply {
            setString(1, id.toString())
        }.executeQuery()

        if (results.next()) {
            return Account(
                    id = id,
                    ownerFirstName = results.getString("owner_first_name"),
                    ownerLastName = results.getString("owner_last_name")
            )
        }
        return null
    }

    open fun findByIdAndLock(connection: Connection, id: AccountId): Account? {
        val results = connection.prepareStatement("""
            SELECT owner_first_name, owner_last_name
            FROM account
            WHERE id = ?
            FOR UPDATE
        """.trimIndent()).apply {
            setString(1, id.toString())
        }.executeQuery()

        if (results.next()) {
            return Account(
                    id = id,
                    ownerFirstName = results.getString("owner_first_name"),
                    ownerLastName = results.getString("owner_last_name")
            )
        }
        return null
    }


}