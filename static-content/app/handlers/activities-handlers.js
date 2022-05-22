import { activityApi, routeApi, sportApi, userApi } from '../api/api.js'
import ActivityDetails from '../components/details/ActivityDetails.js'
import ActivityList from '../components/lists/ActivityList.js'
import {Pagination} from '../components/Pagination.js'
import { H1, Div } from '../components/dsl.js'
import {onPaginationChange} from './app-handlers.js'
import {queryBuilder} from "../api/api-utils.js";
import ActivitySearchFilter from '../components/filters/ActivitySearchFilter.js'
import styles from '../styles.js'


/**
 * Displays an activity list with the given query
 */
async function displayActivityList(mainContent, _, query) {
    const activityList = await activityApi.fetchActivities(query)

    mainContent.replaceChildren(
        H1(styles.HEADER, 'All Activities'),
        ActivityList(activityList.activities),
        Pagination(
            activityList.total,
            (skip, limit) => onPaginationChange("activities", query, skip, limit)
        )
    )

}
/**
 * Displays a list of activities for the given sport with the given query
 */
async function displaySearchActivities(mainContent, params, query) {

    /*
    const [activityList, routeList, sportDetails] = await Promise.all([
        activityApi.fetchActivitiesBySport(params.sid, query),
        routeApi.fetchRoutes(query), // TODO: Change to filter
        sportApi.fetchSport(params.sid)
    ])
    */
    const [routeList, sportList] = await Promise.all([routeApi.fetchRoutes(query),sportApi.fetchSports(query)])
    

    const onFilterSubmit = (date, route, sortOrder) => {
        const newQuery = query 
        if(date) newQuery.date = date
        if(date === '') delete newQuery.date
        if(newQuery.rid !== route){
            newQuery.rid = route
        }
        if(!route) delete newQuery.rid
        if(sortOrder) newQuery.orderBy = sortOrder
        
        window.location.hash = `#sports/${params.sid}/activities?${queryBuilder(newQuery)}`
    } 


    mainContent.replaceChildren(
        Div('activity-header-filter',
            H1(styles.HEADER, `Activity search`),
            ActivitySearchFilter(onFilterSubmit, routeList.routes,sportList.sports, query),
        ),
       
    )
}

/**
 * Displays the activities from the given user with the given query
 */
async function displayActivitiesByUser(mainContent, params, query) {
    const activityList = await activityApi.fetchActivitiesByUser(params.uid, query)
    const user = await userApi.fetchUser(params.uid)
    mainContent.replaceChildren(
        H1(styles.HEADER, `Activities for ${user.name}`),
        ActivityList(activityList.activities),
        Pagination(
            activityList.total,
            (skip, limit) => onPaginationChange(`users/${params.uid}/activities`, query, skip, limit)
        )
    )
}

/**
 * Displays the activity details for the given activity id
 */
async function displayActivityDetails(mainContent, params, _) {
    const activity = await activityApi.fetchActivity(params.sid, params.aid)
    mainContent.replaceChildren(
        ActivityDetails(activity)
    )
}

export const activityHandlers = {
    displaySearchActivities,
    displayActivitiesByUser,
    displayActivityDetails,
    displayActivityList
}
