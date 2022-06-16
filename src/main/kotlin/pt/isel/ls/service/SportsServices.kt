package pt.isel.ls.service

import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.service.inputs.ActivityInputs.validateID
import pt.isel.ls.service.inputs.SportInputs.SportCreateInput
import pt.isel.ls.service.inputs.SportInputs.SportUpdateInput
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.loggerFor
import pt.isel.ls.utils.repository.transactions.TransactionFactory
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireOwnership
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction

class SportsServices(
    private val transactionFactory: TransactionFactory
) {

    companion object {
        private val logger = loggerFor<UserServices>()
        const val NAME_PARAM = "Sport name"
        const val DESCRIPTION_PARAM = "description"
        const val SPORT_ID_PARAM = "sportID"
        const val RESOURCE_NAME = "Sport"
    }

    /**
     * Gets the [Sport] identified by the given id.
     *
     * @param sid the id that identifies the [Sport] to get
     * @return [Sport] the sport identified by the given id
     */
    fun getSport(sid: Param): SportDTO {
        logger.traceFunction(::getSport.name) { listOf(SPORT_ID_PARAM to sid) }

        val sportID = validateID(sid, SPORT_ID_PARAM)

        return transactionFactory.getTransaction().execute {

            sportsRepository.getSport(sportID)?.toDTO()
                ?: throw ResourceNotFound(RESOURCE_NAME, sportID.toString())
        }
    }

    /**
     * Creates and adds a [SportDTO] with the given parameters
     * @param token token the user token to be used to verify the user.
     * @param sport [SportCreateInput] contain the sport data given by the user.
     * @return [SportID] the sport's unique identifier
     */
    fun createSport(token: UserToken?, sport: SportCreateInput): SportID {
        val name = sport.name
        val description = sport.description

        logger.traceFunction(::createSport.name) { listOf(NAME_PARAM to name, DESCRIPTION_PARAM to description) }

        return transactionFactory.getTransaction().execute {
            val userID = usersRepository.requireAuthenticated(token)
            sportsRepository.addSport(name, description, userID)
        }
    }

    /**
     * Gets all the existing sports
     *
     * @return [List] of [SportDTO]
     */
    fun getSports(search: Param, paginationInfo: PaginationInfo): List<SportDTO> {
        logger.traceFunction(::getSports.name) { emptyList() }

        val handledSearch = search?.ifBlank { null }
        return transactionFactory.getTransaction().execute {
            sportsRepository
                .getSports(handledSearch, paginationInfo)
                .map(Sport::toDTO)
        }
    }

    /**
     * Updates the [Sport] identified by the given id with the given parameters
     * @param token token the user token to be used to verify the user.
     * @param sport [SportUpdateInput] data given by the user from updating the sport.
     *
     */
    fun updateSport(token: UserToken?, sport: SportUpdateInput) {
        val sportID = sport.sportID
        val name = sport.name
        val description = sport.description

        logger.traceFunction(::updateSport.name) {
            listOf(
                SPORT_ID_PARAM to sportID,
                NAME_PARAM to name,
                DESCRIPTION_PARAM to description
            )
        }

        // No update needed, don't waste resources
        if (sport.hasNothingToUpdate) return

        return transactionFactory.getTransaction().execute {

            val userId = usersRepository.requireAuthenticated(token)
            sportsRepository.requireOwnership(userId, sportID)

            if (!sportsRepository.updateSport(sportID, name, description))
                throw ResourceNotFound(RESOURCE_NAME, sportID.toString())
        }
    }
}
