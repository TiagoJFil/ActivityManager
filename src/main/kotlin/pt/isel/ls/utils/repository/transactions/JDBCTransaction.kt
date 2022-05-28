package pt.isel.ls.utils.repository.transactions

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class JDBCTransaction(val connection: Connection) : Transaction {

    override val scope = JDBCTransactionScope(this)

    /**
     * Begins the transaction.
     */
    override fun begin() {
        connection.autoCommit = false
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
    override fun <T> execute(block: TransactionScope.() -> T): T {
        return connection.use { super.execute(block) }
    }
}
