package pt.isel.ls.repository.database

import pt.isel.ls.service.transactions.JDBCTransactionScope
import pt.isel.ls.service.transactions.Transaction
import pt.isel.ls.service.transactions.TransactionScope
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class JDBCTransaction(val connection: Connection) : Transaction {

    override val scope: TransactionScope = JDBCTransactionScope(connection)

    /**
     * Sets the isolation level of the transaction.
     */
    private fun setIsolationLevel(level: Transaction.IsolationLevel) {
        connection.transactionIsolation = when (level) {
            Transaction.IsolationLevel.READ_UNCOMMITTED -> Connection.TRANSACTION_READ_UNCOMMITTED
            Transaction.IsolationLevel.READ_COMMITTED -> Connection.TRANSACTION_READ_COMMITTED
            Transaction.IsolationLevel.REPEATABLE_READ -> Connection.TRANSACTION_REPEATABLE_READ
            Transaction.IsolationLevel.SERIALIZABLE -> Connection.TRANSACTION_SERIALIZABLE
        }
    }

    /**
     * Begins the transaction.
     */
    override fun begin(level: Transaction.IsolationLevel) {
        connection.autoCommit = false
        setIsolationLevel(level)
    }

    /**
     * Commits the changes made in the transaction.
     */
    override fun commit() {
        connection.commit()
    }

    /**
     * Rolls back the changes made in the transaction.
     */
    override fun rollback() {
        connection.rollback()
    }

    /**
     * Ends the transaction.
     */
    override fun end() {
        connection.autoCommit = true
    }

    /**
     * Creates a transaction on the given connection properly allocating and disposing connection resources.
     * Resource management for [Statement]s and [ResultSet]s have to be done by the caller in [block].
     *
     * Commits on success.
     * Rolls back if an exception is thrown propagating it afterwards.
     *
     * @param block the block to execute in the transaction
     * @return the result of the block function invoked in the transaction
     */
    override fun <T> execute(
        level: Transaction.IsolationLevel,
        block: TransactionScope.() -> T
    ): T {
        return connection.use { super.execute(level, block) }
    }
}
