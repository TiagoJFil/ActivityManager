import {sportApi} from '../api/api.js'
import SportDetails from '../components/details/SportDetails.js'
import SportList from '../components/lists/SportList.js'
import {Pagination} from '../components/Pagination.js'
import {onPaginationChange} from './app-handlers.js'
import SportFilter from '../components/filters/SportFilter.js'
import styles from '../styles.js'
import {H1, Div, Icon, Anchor} from '../components/dsl.js'
import SportCreate from "../components/creates/CreateSport.js";

/**
 * Displays a sport list with the given query
 */
async function displaySportList(mainContent, _, query) {
    const sportsList = await sportApi.fetchSports(query)
    const withHeader = false

    const onSportTextChange = () => {
    }

    const onSubmit = (searchText) => {
        window.location.hash = `sports?search=${searchText}`
    }

    const sportFilterBar = SportFilter(onSportTextChange, onSubmit, withHeader)

    const addAnchor = Anchor("big",`#sports/add`, Icon(styles.BX_CLASS, styles.ADD_ICON))
    addAnchor.title = "Add a sport"
    sportFilterBar.prepend(addAnchor)

    mainContent.replaceChildren(
        Div(styles.SEARCH_AND_HEADER_DIV,
            H1(styles.HEADER, 'Sports'),
            sportFilterBar
        ),
        SportList(sportsList.sports),
        Pagination(sportsList.total, (skip, limit) => onPaginationChange("sports", query, skip, limit))
    )
}

/**
 * Displays a sport details with the given id
 */
async function displaySportDetails(mainContent, params, _) {

    const sport = await sportApi.fetchSport(params.sid)

    mainContent.replaceChildren(
        SportDetails(sport)
    )

}

async function displaySportSearch(mainContent, params, _) {

    const onSubmit = (searchText) => {
        window.location.hash = `sports?search=${searchText}`
    }
    const withHeader = true

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Sport Search'),
        Div(styles.SEARCH_CONTAINER,
            SportFilter(null, onSubmit, withHeader)
        )
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
    displaySportSearch,
    createSport
}
