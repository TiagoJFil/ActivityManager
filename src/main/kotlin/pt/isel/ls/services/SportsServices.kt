package pt.isel.ls.services


import pt.isel.ls.entities.Sport
import pt.isel.ls.entities.SportID
import pt.isel.ls.repository.SportRepository


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
    fun getSports(): List<Sport> =
        sportsRepository.getSports()



}
