package pt.isel.ls.service.inputs.SportInputs

import pt.isel.ls.service.SportsServices
import pt.isel.ls.service.inputs.ActivityInputs.validateID
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.requireNotBlankParameter

class SportUpdateInput(sid: Param, sportName: Param, sportDescription: Param) {

    val sportID: Int
    val name: Param
    val description: Param
    val hasNothingToUpdate: Boolean

    init {
        sportID = validateID(sid, SportsServices.SPORT_ID_PARAM)
        name = requireNotBlankParameter(sportName, SportsServices.NAME_PARAM)
        description = sportDescription
        hasNothingToUpdate = (sportName == null || sportName.isBlank()) && (description == null)
    }
}
