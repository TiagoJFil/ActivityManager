import {Div, H1, Text} from "../components/dsl.js"
import { sportHandlers } from "./sports-handlers.js";
import { userHandlers } from "./users-handlers.js";
import { routeHandlers } from "./routes-handlers.js";
import { activityHandlers } from "./activities-handlers.js";
import styles from "../styles.js";

const NOT_FOUND_MESSAGE = 'Sorry, the page you are looking for does not exist. Try heading to the home page.'

/**
 * Displays the home page 
 */
function getHome(mainContent) {
    const h1 = H1(styles.HEADER, 'Home')
    mainContent.replaceChildren(
        h1,
        Text(styles.TEXT, 'WELCOME!')
    )
}


/**
 * Displays a page to indicate that nothing was found
 */
function getNotFoundPage(mainContent) {

    mainContent.replaceChildren(
        H1(styles.HEADER,'404 - Not Found'),
        Text(styles.TEXT, NOT_FOUND_MESSAGE),
    )
}

/**
 * Displays the error page with the given error message
 */
function getErrorPage(mainContent, error) {
    let message;
    let header;
    
    switch(error.code){
        case 2002:
            header = 'Content Not Found'
            message = NOT_FOUND_MESSAGE
            break
        default:
            header = 'Error'
            message = 'An error has occurred. Please try again later.'
            break
    }
    mainContent.replaceChildren(
        H1(styles.HEADER, header),
        Text(styles.TEXT, message),
        Div("empty","")
    )
}

/**
 * The function to be called when the pagination is changed
 * 
 * @param {string} path  the path of the page to be loaded
 * @param {Text} currentQuery the current query
 * @param {Number} skip  the skip to be used in the query
 * @param {Number} limit   the limit to be used in the query
 */
 export function onPaginationChange(path, currentQuery, skip, limit){

    const query = currentQuery ? currentQuery : {}

    query.skip = skip
    query.limit = limit
    
    window.location.hash = `#${path}?${queryBuilder(query)}`
}

/**
 * Builds the query string from the given query object
 * @param {Object} queryValues 
 * @returns {String} the query as String
 */
export function queryBuilder(queryValues){

    if(queryValues === undefined) return ''

    return Object
        .keys(queryValues)
        .map(key => `${key}=${queryValues[key]}`)
        .join('&')
}

export default {
    getHome,
    getNotFoundPage,
    getErrorPage,
    getSports: sportHandlers.displaySportList,
    getSport: sportHandlers.displaySportDetails,
    getUsers: userHandlers.displayUserList,
    getUser: userHandlers.displayUserDetails,
    getRoutes: routeHandlers.displayRouteList,
    getRoute: routeHandlers.displayRouteDetails,
    getActivities: activityHandlers.displayActivityList,
    getActivity: activityHandlers.displayActivityDetails,
    getUsersByActivity: userHandlers.displayUsersByActivity,
    getActivitiesByUser: activityHandlers.displayActivitiesByUser,
    getActivitiesBySport: activityHandlers.displayActivitiesBySport
}


