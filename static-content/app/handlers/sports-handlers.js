import {sportApi} from '../api.js'
import SportDetails from '../components/details/SportDetails.js'
import SportList from '../components/lists/SportList.js'
import {Pagination, getPaginationQuery} from '../components/Pagination.js'
import {queryBuilder, onPaginationChange} from './app-handlers.js'
import styles from '../styles.js'
import { H1 } from '../components/dsl.js'

/**
 * Displays a sport list with the given query
 */
async function displaySportList(mainContent, _, query) {
    const paginationQuery = queryBuilder(query)
    const sports = await sportApi.fetchSports(paginationQuery)
    const sportCount = await sportApi.fetchSportsCount()
    

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Sports'),
        SportList(sports),
        Pagination(sportCount, (skip, limit) => onPaginationChange("sports", query, skip, limit))
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

export const sportHandlers = {
    displaySportDetails,
    displaySportList,
}
