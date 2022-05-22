import {sportApi} from '../api/api.js'
import SportDetails from '../components/details/SportDetails.js'
import SportList from '../components/lists/SportList.js'
import {getItemsPerPage, Pagination} from '../components/Pagination.js'
import {onPaginationChange} from './app-handlers.js'
import styles from '../styles.js'
import {H1, Div, Icon, Anchor} from '../components/dsl.js'
import SportCreate from "../components/creates/CreateSport.js";
import SearchBar from  "../components/SearchBar.js";
import { SuccessToast, ErrorToast ,InfoToast} from '../toasts.js'

/**
 * Displays a sport list with the given query
 */
async function displaySportList(mainContent, _, query) {
    const sportsList = await sportApi.fetchSports(query)
    let listElement = SportList(sportsList.sports)

    let paginationElement = Pagination(sportsList.total,
        (skip, limit) => onPaginationChange("sports", query, skip, limit),
        query.limit
    )

    const onSportTextChange = async (searchText) => {

        const newQuery = {
            search: searchText,
            limit: query.limit ?? getItemsPerPage(),
            skip: 0
        }

        const innerSportsList = await sportApi.fetchSports(newQuery)
        const newPagination = Pagination(
            innerSportsList.total,
            (skip, limit) => onPaginationChange("sports", query, skip, limit),
            query.limit
        )
        const newListElement = SportList(innerSportsList.sports)

        listElement.replaceWith(
            newListElement
        )
        paginationElement.replaceWith(
            newPagination
        )

        listElement = newListElement
        paginationElement = newPagination
    }

    const addAnchor = Anchor("big",`#sports/add`, Icon(styles.BX_CLASS, styles.ADD_ICON))
    addAnchor.title = "Add a sport"

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Sports'),
        Div(styles.SEARCH_BAR_WITH_ADD,
            SearchBar("searchRes", styles.FORM_TEXT_INPUT, onSportTextChange, "Search for a sport", null),
            addAnchor
        ),
        listElement,
        paginationElement
    )
}

/**
 * Displays a sport details with the given id
 */
async function displaySportDetails(mainContent, params, _) {

    const sport = await sportApi.fetchSport(params.sid)
    const onEditConfirm = async (name,description) => {

        return sportApi.updateSport(sport.id, name, description)
        .then(() => {
            SuccessToast("Saved!").showToast()
            return true
        }).catch((e) => {
            ErrorToast("Error updating sport").showToast()
            InfoToast(e.message).showToast()
            return false
        })

    }
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Sport Details'),
        SportDetails(sport, onEditConfirm),
        Div(styles.SPACER),
        Div(styles.SPACER),
        Div(styles.SPACER)
    )

}

/**
 * Creates a new sport
 */
async function createSport(mainContent, params, _) {

    const onSubmit = async (name, description) => {
        try{
            await sportApi.createSport(name, description)
        }catch(e){
            let message = ""
            if(e.code === 2000)
                message = "Sport's name should be between 1-30 characters."
            else if(e.code === 2003)
                message = "Try logging in to create a sport."

            Toastify({
                text: message,
                backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)",
                oldestFirst: false,
            }).showToast()
            return
        }

        window.location.hash = 'sports'
    }

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Add a Sport'),
        Div(styles.ADD_CONTAINER,
            SportCreate(onSubmit)
        )
    )
}

export const sportHandlers = {
    displaySportDetails,
    displaySportList,
    createSport
}





