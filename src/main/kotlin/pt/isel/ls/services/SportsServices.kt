package pt.isel.ls.services

import pt.isel.ls.entities.Sport
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

class SportsServices(
    val sportsRepository: SportRepository
) {
    fun createSport(userID: UserID, name: String?, description: String?): SportID {
        requireNotNull(name) { "Required parameter name." }
        val sport = Sport(generateRandomId(), name, description, userID)
        sportsRepository.addSport(sport)

        return sport.id
    }

    fun getSports(): List<Sport> =
        sportsRepository.getSports()

    fun getSportsByID(sportID: SportID): Sport {
        TODO()
    }
}
