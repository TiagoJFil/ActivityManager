package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.entities.Sport
import pt.isel.ls.http.sportsRoutes
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

class SportsServices(
    val sportsRepository: SportRepository
) {

    /**
     * Gets the [Sport] identified by the given id.
     *
     * @param sportID the id that identifies the [Sport] to get
     * @return [Sport] the sport identified by the given id
     */
    fun getSport(sportID: SportID?): Sport{
       requireNotNull(sportID) {" id must not be null"}
       require(sportID.isNotBlank()) {" id field has no value "}
       val sport: Sport? = sportsRepository.getSportByID(sportID)
       checkNotNull(sport)
       return sport
    }

    /**
     * Gets all the existing sports
     *
     * @return [List] of [Sport]
     */
    fun createSport(userID: UserID, name: String?, description: String?): SportID {
        requireNotNull(name) { "Required parameter name." }
        val sport = Sport(generateRandomId(), name, description, userID)
        sportsRepository.addSport(sport)

        return sport.id
    }

    fun getSports(): List<Sport> =
        sportsRepository.getSports()



}
