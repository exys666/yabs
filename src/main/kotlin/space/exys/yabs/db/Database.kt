package space.exys.yabs.db

import java.sql.Connection

interface Database {

    val connection: Connection
}