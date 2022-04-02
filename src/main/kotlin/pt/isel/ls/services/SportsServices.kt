package pt.isel.ls.services

import pt.isel.ls.services.dto.SportDTO
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.utils.*


class SportsServices(
    private val sportsRepository: SportRepository,
    private val userRepository: UserRepository
) {
    companion object{
        val logger = getLoggerFor<UserServices>()
    }
    /**
     * Gets the [Sport] identified by the given id.
     *
     * @param sportID the id that identifies the [Sport] to get
     * @return [Sport] the sport identified by the given id
     */
    fun getSport(sportID: SportID?): SportDTO {
        logger.traceFunction("getSport","sportID = $sportID")
        val safeSportID = requireParameter(sportID, "sportID")
        return sportsRepository.getSportByID(safeSportID)?.toDTO()
                ?: throw ResourceNotFound("Sport", "$sportID")
    }

    /**
     * Creates and adds a [SportDTO] with the given parameters
     * @param token token the user token to be used to verify the user.
     * @param name the name of the [SportDTO] to be created.
     * @param description the description of the [SportDTO] to be created.
     * @return [SportID] the sport's unique identifier
     */
    fun createSport(token: UserToken?, name: String?, description: String?): SportID {
        logger.traceFunction("createSport","name =$name","description = $description")
        val userID = userRepository.requireAuthenticated(token)

        val safeName = requireParameter(name, "name")
        val handledDescription = description?.ifBlank { null }
        val sportID = generateRandomId()
        sportsRepository.addSport(sportID, safeName, handledDescription, userID)
        return sportID
    }
    /**
     * Gets all the existing sports
     *
     * @return [List] of [SportDTO]
     */
    fun getSports(): List<SportDTO> {
        logger.traceFunction("getSports")
        return sportsRepository
            .getSports()
            .map(Sport::toDTO)
    }

}


