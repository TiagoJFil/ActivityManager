package pt.isel.ls.utils.repository.transactions

object InMemoryTransaction : Transaction {

    override val scope: TransactionScope = InMemoryTransactionScope

    override fun begin() {}
    override fun commit() {}
    override fun rollback() {}
    override fun end() {}
}
