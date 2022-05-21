import { routeApi } from '../api/api.js'
import RouteList from '../components/lists/RouteList.js'
import RouteDetails from '../components/details/RouteDetails.js'
import {Pagination} from '../components/Pagination.js'
import { onPaginationChange} from './app-handlers.js'
import styles from '../styles.js'
import { H1, Div } from '../components/dsl.js'
import RouteFilter from "../components/filters/RouteFilter.js";


/**
 * Displays a route list with the given query
 */
async function displayRouteList(mainContent, _, query) {

    const routeList = await routeApi.fetchRoutes(query);
    
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Routes'),
        RouteList(routeList.routes),
        Pagination(routeList.total, (skip, limit) => onPaginationChange("routes", query, skip, limit))
    )
    
}

/**
 * Displays a route details with the given id
 */
async function displayRouteDetails(mainContent, params, _) {

    const route = await routeApi.fetchRoute(params.rid)

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Route Details'),
        RouteDetails(route),
        Div(styles.SPACER)
    )
}

/**
 * Displays the interface to search for a route
 */
async function searchRoutesDisplay(mainContent, params, _) {

    const onSubmit = (sLocation, eLocation) =>{
        window.location.hash = `routes?startLocation=${sLocation}&endLocation=${eLocation}`
    }

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Route Search'),
        Div(styles.SEARCH_ROUTE,
            RouteFilter(onSubmit)
        ),
        Div(styles.SPACER),
        Div(styles.SPACER),
        Div(styles.SPACER)
    )
}

export const routeHandlers = {
    displayRouteDetails,
    displayRouteList,
    searchRoutesDisplay
}