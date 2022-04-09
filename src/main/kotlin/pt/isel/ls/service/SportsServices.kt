package pt.isel.ls.service

import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.utils.service.toDTO
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserToken
import pt.isel.ls.utils.getLoggerFor
import pt.isel.ls.utils.service.requireAuthenticated
import pt.isel.ls.utils.service.requireParameter
import pt.isel.ls.utils.traceFunction

class SportsServices(
    private val sportsRepository: SportRepository,
    private val userRepository: UserRepository
) {

    companion object {
        val logger = getLoggerFor<UserServices>()
        const val NAME_PARAM = "name"
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
    fun getSport(sid: SportID?): SportDTO {
        logger.traceFunction(::getSport.name) { listOf(SPORT_ID_PARAM to sid) }

        val safeSportID = requireParameter(sid, SPORT_ID_PARAM)
        return sportsRepository.getSportByID(safeSportID)?.toDTO()
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
    fun getSports(): List<SportDTO> {
        logger.traceFunction(::getSports.name) { emptyList() }

        return sportsRepository
            .getSports()
            .map(Sport::toDTO)
    }
}
