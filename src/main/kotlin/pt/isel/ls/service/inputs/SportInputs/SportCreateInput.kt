package pt.isel.ls.service.inputs.SportInputs

import pt.isel.ls.service.SportsServices
import pt.isel.ls.utils.Param
import pt.isel.ls.utils.service.requireParameter

class SportCreateInput(sportName: Param, sportDescription: Param) {

    val name: String
    val description: Param

    init {
        name = requireParameter(sportName, SportsServices.NAME_PARAM)
        description = sportDescription?.ifBlank { null }
    }
}
