import {sportApi} from '../api.js'
import SportDetails from '../components/details/SportDetails.js'
import SportList from '../components/lists/SportList.js'
import {Pagination} from '../components/Pagination.js'
import {queryBuilder, onPaginationChange} from './app-handlers.js'
import SportFilter from '../components/filters/SportFilter.js'
import styles from '../styles.js'
import {H1, Div} from '../components/dsl.js'
import {LinkIcon} from "../components/LinkIcon.js";
import SportCreate from "../components/creates/CreateSport.js";

/**
 * Displays a sport list with the given query
 */
async function displaySportList(mainContent, _, query) {
    const paginationQuery = queryBuilder(query)
    const sports = await sportApi.fetchSports(paginationQuery)
    const withHeader = false

    const onSportTextChange = () => {
    }

    const onSubmit = (searchText) => {
        window.location.hash = `sports?search=${searchText}`
    }

    mainContent.replaceChildren(
        Div(styles.SEARCH_AND_HEADER_DIV,
            H1(styles.HEADER, 'Sports'),
            SportFilter(onSportTextChange, onSubmit, withHeader)
        ),

        SportList(sports),

        Div(styles.ADD_BUTTON_CONTAINER,
            LinkIcon(styles.ADD_ICON,`#sports/add`, "Add a sport")
        ),

        Pagination(sports.length, (skip, limit) => onPaginationChange("sports", query, skip, limit))
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

    const onSportTextChange = () => {
    }

    const onSubmit = (searchText) => {
        window.location.hash = `sports?search=${searchText}`
    }
    const withHeader = true

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Sport Search'),
        Div(styles.SEARCH_CONTAINER,
            SportFilter(onSportTextChange, onSubmit, withHeader)
        )
    )
}

/**
 * Creates a new sport
 */
async function createSport(mainContent, params, _) {

    const onSubmit = (name, description) => {
        sportApi.createSport(name, description)
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
