package pt.isel.ls.config

import org.postgresql.ds.PGSimpleDataSource
import org.postgresql.util.PSQLException
import pt.isel.ls.utils.repository.transactions.InMemoryTransactionFactory
import pt.isel.ls.utils.repository.transactions.JDBCTransactionFactory
import pt.isel.ls.utils.repository.transactions.TransactionFactory

enum class DBMODE { MEMORY, POSTGRESQL }

fun DBMODE.transactionFactory(): TransactionFactory =
    when (this) {
        DBMODE.MEMORY -> InMemoryTransactionFactory
        DBMODE.POSTGRESQL -> postgreSQLTransactionFactory()
    }

private fun postgreSQLTransactionFactory(): TransactionFactory {

    val dbInfo = dbConnectionInfo()

    val dataSource = PGSimpleDataSource().apply {
        setURL(dbInfo.url)
        user = dbInfo.user
        password = dbInfo.password
        databaseName = dbInfo.dataBase
    }

    try {
        dataSource.connection
    } catch (e: PSQLException) {
        throw IllegalArgumentException(
            "Could not connect to database with the information given. " +
                "Please check your JDBC environment variables or the connectivity to the database."
        )
    }

    return JDBCTransactionFactory(dataSource)
}
