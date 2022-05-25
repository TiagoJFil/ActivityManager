import {Div, H1, Text, Image} from "../components/dsl.js"
import { sportHandlers } from "./sports-handlers.js";
import { userHandlers } from "./users-handlers.js";
import { routeHandlers } from "./routes-handlers.js";
import { activityHandlers } from "./activities-handlers.js";
import { queryBuilder } from "../api/api-utils.js";
import styles from "../styles.js";



/**
 * Displays the home page 
 */
function getHome(mainContent) {

    mainContent.replaceChildren(
        Div("home-page",
            H1(styles.HEADER, 'Sports Isel'),
            Div("image-group", 
                Image("home-image", "homeImage", "./img/running.svg"),
                Image("home-image", "homeImageGirl", "./img/running-girl.svg")
            )
        )
    )

}

const NOT_FOUND_MESSAGE = 'Sorry, the page you are looking for does not exist. Try heading to the home page.'

/**
 * Displays a page to indicate that nothing was found
 */
function getNotFoundPage(mainContent) {

    mainContent.replaceChildren(
        H1(styles.HEADER,'404 - Not Found'),
        Text(styles.TEXT, NOT_FOUND_MESSAGE),
        Div(styles.SPACER)
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
        Div(styles.SPACER)
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

    const query = currentQuery ? {...currentQuery} : {}

    query.skip = skip
    query.limit = limit
    
    window.location.hash = `#${path}?${queryBuilder(query)}`
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
    createActivity: activityHandlers.createActivity,
    getActivities: activityHandlers.displayActivityList,
    getActivity: activityHandlers.displayActivityDetails,
    getUsersByActivity: userHandlers.displayUsersByActivity,
    getActivitiesByUser: activityHandlers.displayActivitiesByUser,
    getActivitiesSearch: activityHandlers.displaySearchActivities,
    getUsersByRanking: userHandlers.displayUsersByRanking,
    createSport: sportHandlers.createSport,
    createRoute: routeHandlers.createRoute
}


