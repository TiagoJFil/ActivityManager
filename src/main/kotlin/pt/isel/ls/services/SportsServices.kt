package pt.isel.ls.services

import pt.isel.ls.entities.Sport
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
    fun getSport(sportID: SportID?): Sport {
        val safeSportID = requireParameter(sportID, "sportID")
        return sportsRepository.getSportByID(safeSportID) ?: throw ResourceNotFound("Sport", "$sportID")
    }

    /**
     * Creates and adds a [Sport] with the given parameters
     *
     * @return [SportID] the sport's unique identifier
     */
    fun createSport(userID: UserID, name: String?, description: String?): SportID {
        val safeName = requireParameter(name, "name")
        val handledDescription = description?.ifBlank { null }
        val sport = Sport(generateRandomId(), safeName, handledDescription, userID)
        sportsRepository.addSport(sport)
        return sport.id
    }

    /**
     * Gets all the existing sports
     *
     * @return [List] of [Sport]
     */
    fun getSports(): List<Sport> =
        sportsRepository.getSports()



}
