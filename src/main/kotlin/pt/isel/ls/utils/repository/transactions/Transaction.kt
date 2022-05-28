package pt.isel.ls.utils.repository.transactions

/**
 * Represents an app transaction.
 *
 */
sealed interface Transaction {

    val scope: TransactionScope

    /**
     * Begins the transaction.
     */
    fun begin()

    /**
     * Commits the changes made in the transaction.
     */
    fun commit()

    /**
     * Rolls back the changes made in the transaction.
     */
    fun rollback()

    /**
     * Ends the transaction.
     */
    fun end()

    /**
     * Executes the given [block] in a transaction scope.
     *
     * Meaning that the changes made in the transaction to repositories covered by the [TransactionScope]
     * will be committed if the block completes without errors,
     * or rolled back if the block throws an exception.
     *
     * The exception thrown by the block is propagated after rollback.
     */
    fun <T> execute(block: TransactionScope.() -> T): T {
        begin()
        try {
            val result = scope.block()
            commit()
            return result
        } catch (e: Exception) {
            rollback()
            throw e
        } finally {
            end()
        }
    }
}
