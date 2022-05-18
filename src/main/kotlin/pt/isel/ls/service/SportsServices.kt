package pt.isel.ls.service

import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireIdInteger
import pt.isel.ls.utils.service.requireNotBlankParameter
import pt.isel.ls.utils.service.requireOwnership
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.utils.traceFunction

class SportsServices(
    private val sportsRepository: SportRepository,
    private val userRepository: UserRepository
) {

    companion object {
        private val logger = getLoggerFor<UserServices>()
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

        val safeSportID = requireParameter(sid, SPORT_ID_PARAM)
        val sidInt: SportID = requireIdInteger(safeSportID, SPORT_ID_PARAM)
        return sportsRepository.getSport(sidInt)?.toDTO()
            ?: throw ResourceNotFound(RESOURCE_NAME, safeSportID)
    }

    /**
     * Creates and adds a [SportDTO] with the given parameters
     * @param token token the user token to be used to verify the user.
     * @param name the name of the [SportDTO] to be created.
     * @param description the description of the [SportDTO] to be created.
     * @return [SportID] the sport's unique identifier
     */
    fun createSport(token: UserToken?, name: String?, description: String?): SportID {
        logger.traceFunction(::createSport.name) { listOf(NAME_PARAM to name, DESCRIPTION_PARAM to description) }

        val userID = userRepository.requireAuthenticated(token)
        val safeName = requireParameter(name, NAME_PARAM)
        val handledDescription = description?.ifBlank { null }

        return sportsRepository.addSport(safeName, handledDescription, userID)
    }
    /**
     * Gets all the existing sports
     *
     * @return [List] of [SportDTO]
     */
    fun getSports(search: Param, paginationInfo: PaginationInfo): List<SportDTO> {
        logger.traceFunction(::getSports.name) { emptyList() }

        return sportsRepository
            .getSports(search, paginationInfo)
            .map(Sport::toDTO)
    }

    /**
     * Updates the [Sport] identified by the given id with the given parameters
     * @param token token the user token to be used to verify the user.
     * @param sid the id that identifies the [Sport] to update
     * @param name the name of the [Sport] to be updated.
     * @param description the description of the [Sport] to be updated.
     *
     */
    fun updateSport(token: UserToken?, sid: Param, name: Param, description: Param) {
        logger.traceFunction(::updateSport.name) {
            listOf(
                SPORT_ID_PARAM to sid,
                NAME_PARAM to name,
                DESCRIPTION_PARAM to description
            )
        }

        val userId = userRepository.requireAuthenticated(token)

        val safeSportID = requireParameter(sid, SPORT_ID_PARAM)
        val sidInt: SportID = requireIdInteger(safeSportID, SPORT_ID_PARAM)

        sportsRepository.requireOwnership(userId, sidInt)

        if ((name == null || name.isBlank()) && (description == null)) return
        // No update needed, don't waste resources
        requireNotBlankParameter(name, NAME_PARAM)

        if (!sportsRepository.updateSport(sidInt, name, description))
            throw ResourceNotFound(RESOURCE_NAME, safeSportID)
    }
}
