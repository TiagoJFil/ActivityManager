package pt.isel.ls.config

import org.postgresql.ds.PGSimpleDataSource
import org.postgresql.util.PSQLException
import pt.isel.ls.service.transactions.InMemoryTransactionFactory
import pt.isel.ls.service.transactions.JDBCTransactionFactory
import pt.isel.ls.service.transactions.TransactionFactory

enum class DBMODE {
    MEMORY, POSTGRESQL;

    val transactionFactory: TransactionFactory
        get() = when (this) {
            MEMORY -> InMemoryTransactionFactory
            POSTGRESQL -> postgreSQLTransactionFactory()
        }
}

private fun postgreSQLTransactionFactory(): TransactionFactory {

    val dbInfo = dbConnectionInfo()

    val dataSource = PGSimpleDataSource().apply {
        setURL(dbInfo.url)
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
