package pt.isel.ls.service.transactions

import pt.isel.ls.repository.database.JDBCTransaction
import pt.isel.ls.repository.memory.InMemoryTransaction
import javax.sql.DataSource

/**
 * A factory for creating [Transaction] instances.
 */
interface TransactionFactory {

    /**
     * Creates a new [Transaction]
     *
     * @return a new [Transaction] instance.
     */
    fun getTransaction(): Transaction
}

class JDBCTransactionFactory(val dataSource: DataSource) : TransactionFactory {
    /**
     * Creates a new [Transaction]
     *
     * @return a new [Transaction] instance.
     */
    override fun getTransaction(): Transaction = JDBCTransaction(dataSource.connection)
}

object InMemoryTransactionFactory : TransactionFactory {

    /**
     * Creates a new [Transaction] instance
     */
    override fun getTransaction(): Transaction = InMemoryTransaction
}
