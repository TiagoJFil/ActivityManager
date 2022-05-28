package pt.isel.ls.utils.repository.transactions

import javax.sql.DataSource

/**
 * A factory for creating [Transaction] instances.
 *
 * @param dataSource the data source to use for creating transactions.
 */
interface TransactionFactory {

    /**
     * Creates a new [Transaction] instance based on the [dataSource] provided.
     *
     * @return a new [Transaction] instance.
     */
    fun getTransaction(): Transaction
}

class JDBCTransactionFactory(val dataSource: DataSource) : TransactionFactory {
    /**
     * Creates a new [Transaction] instance based on the [dataSource] provided.
     *
     * @return a new [Transaction] instance.
     */
    override fun getTransaction(): Transaction = JDBCTransaction(dataSource.connection)
}

object InMemoryTransactionFactory : TransactionFactory {

    /**
     * Creates a new [Transaction] instance based on the [dataSource] provided.
     *
     * @return a new [Transaction] instance.
     */
    override fun getTransaction(): Transaction = InMemoryTransaction
}
