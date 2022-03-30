package pt.isel.ls.services

import pt.isel.ls.services.dto.SportDTO
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.utils.*


class SportsServices(
    private val sportsRepository: SportRepository
) {

    /**
     * Gets the [Sport] identified by the given id.
     *
     * @param sportID the id that identifies the [Sport] to get
     * @return [Sport] the sport identified by the given id
     */
    fun getSport(sportID: SportID?): SportDTO {
        val safeSportID = requireParameter(sportID, "sportID")
        return sportsRepository.getSportByID(safeSportID)?.toDTO()
                ?: throw ResourceNotFound("Sport", "$sportID")
    }

    /**
     * Creates and adds a [Sport] with the given parameters
     *
     * @return [SportID] the sport's unique identifier
     */
    fun createSport(userID: UserID, name: String?, description: String?): SportID {
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
    fun getSports(): List<SportDTO> =
            sportsRepository
                    .getSports()
                    .map(Sport::toDTO)
}


