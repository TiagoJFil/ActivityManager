import {sportApi} from '../api.js'
import SportDetails from '../components/details/SportDetails.js'
import SportList from '../components/lists/SportList.js'
import {Pagination, getPaginationQuery} from '../components/Pagination.js'
import {queryBuilder, onPaginationChange} from './app-handlers.js'
import SportFilter from '../components/filters/SportFilter.js'
import styles from '../styles.js'
import { H1, Div, Text, Input} from '../components/dsl.js'

/**
 * Displays a sport list with the given query
 */
async function displaySportList(mainContent, _, query) {
    const paginationQuery = queryBuilder(query)
    const sports = await sportApi.fetchSports(paginationQuery)
    const withHeader = false
    
    const onSportTextChange = () => {} 

    const onSubmit = (searchText) =>{
        window.location.hash = `sports?search=${searchText}`
    }
    
    
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Sports'),
        Div('sport-search-list',
            Div("sport-filter-div", 
                SportFilter(onSportTextChange, onSubmit, withHeader)
            ),
            SportList(sports)
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
    
    const onSportTextChange = () => {} 

    const onSubmit = (searchText) =>{
        window.location.hash = `sports?search=${searchText}`
    }
    const withHeader = true

    mainContent.replaceChildren(
        Div("sport-filter", 
            H1(styles.HEADER, 'Sport Search'),
            SportFilter(onSportTextChange, onSubmit, withHeader)
        )
    )   
}

export const sportHandlers = {
    displaySportDetails,
    displaySportList,
    displaySportSearch
}
