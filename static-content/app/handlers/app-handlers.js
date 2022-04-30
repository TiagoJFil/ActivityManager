import {Div, H1, Text, Button} from "../components/dsl.js"
import { sportHandlers } from "./sports-handlers.js";
import { userHandlers } from "./users-handlers.js";
import { routeHandlers } from "./routes-handlers.js";
import { activityHandlers } from "./activities-handlers.js";
import styles from "../styles.js";

const NOT_FOUND_MESSAGE = 'Sorry, the page you are looking for does not exist. Try heading to the home page.'


function getHome(mainContent) {
    const h1 = H1(styles.HEADER, 'Home')
    mainContent.replaceChildren(
        h1,
        Text(styles.TEXT, 'WELCOME!')
    )
}

export function onPaginationChange(path, currentQuery, skip, limit){

    const query = currentQuery ? currentQuery : {}

    query.skip = skip
    query.limit = limit
    
    window.location.hash = `#${path}?${queryBuilder(query)}`
}

export function queryBuilder(queryValues){

    if(queryValues === undefined) return ''

    return Object
        .keys(queryValues)
        .map(key => `${key}=${queryValues[key]}`)
        .join('&')
}

function getNotFoundPage(mainContent) {

    mainContent.replaceChildren(
        H1(styles.HEADER,'404 - Not Found'),
        Text(styles.TEXT, NOT_FOUND_MESSAGE),
    )
}

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


