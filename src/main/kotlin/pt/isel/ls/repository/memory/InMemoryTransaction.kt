package pt.isel.ls.repository.memory

import pt.isel.ls.service.transactions.InMemoryTransactionScope
import pt.isel.ls.service.transactions.Transaction
import pt.isel.ls.service.transactions.TransactionScope

object InMemoryTransaction : Transaction {

    override val scope: TransactionScope = InMemoryTransactionScope

    /**
     * Begins the transaction.
     */
    override fun begin(level: Transaction.IsolationLevel) {}
    override fun commit() {}
    override fun rollback() {}
    override fun end() {}
}
