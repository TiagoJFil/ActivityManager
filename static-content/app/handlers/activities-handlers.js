import { activityApi, routeApi, sportApi, userApi } from '../api.js'
import ActivityDetails from '../components/details/ActivityDetails.js'
import ActivityList from '../components/lists/ActivityList.js'
import {Pagination, getPaginationQuery} from '../components/Pagination.js'
import { H1, Div } from '../components/dsl.js'
import {queryBuilder, onPaginationChange} from './app-handlers.js'
import ActivitySearchFilter from '../components/ActivitySearchFilter.js'
import styles from '../styles.js'

/**
 * Displays an activity list with the given query
 */
async function displayActivityList(mainContent, _, query) {
    const activities = await activityApi.fetchActivities(queryBuilder(query) || getPaginationQuery())
    const activityCount = await activityApi.fetchActivitiesCount()

    mainContent.replaceChildren(
        H1(styles.HEADER, 'All Activities'),
        ActivityList(activities),
        Pagination(activityCount, (skip, limit) => onPaginationChange("activities", query, skip, limit))
    )
}
/**
 * Displays a list of activities for the given sport with the given query
 */
async function displayActivitiesBySport(mainContent, params, query) {
    const activities = await activityApi.fetchActivitiesBySport(params.sid, queryBuilder(query) || getPaginationQuery())
    const queryForCount = {...query}
    if(queryForCount){
        queryForCount.skip = 0
        queryForCount.limit = 1000000
    }

    const activitiesCount = await activityApi.fetchActivitiesBySportCount(params.sid, queryBuilder(queryForCount)) 
    const routes = await routeApi.fetchRoutes(`limit=1000000`)
    const sport = await sportApi.fetchSport(params.sid)  

    const onFilterSubmit = (date, route, sortOrder) => {
        const newQuery = query 
        if(date) newQuery.date = date
        if(date === '') delete newQuery.date
        if(newQuery.rid != route){
            newQuery.rid = route
        }
        if(!route) delete newQuery.rid
        if(sortOrder) newQuery.orderBy = sortOrder
        
        window.location.hash = `#sports/${params.sid}/activities?${queryBuilder(newQuery)}`
    } 


    mainContent.replaceChildren(
        Div('activity-header-filter',
            H1(styles.HEADER, `Activities for ${sport.name}`),
            ActivitySearchFilter(onFilterSubmit, routes, query),
        ),
        ActivityList(activities),
        Pagination(activitiesCount, (skip, limit) => onPaginationChange(`sports/${params.sid}/activities`, query, skip, limit))
    )
}

/**
 * Displays the activities from the given user with the given query
 */
async function displayActivitiesByUser(mainContent, params, query) {
    const activities = await activityApi.fetchActivitiesByUser(params.uid, queryBuilder(query) || getPaginationQuery())
    const activityCount = await activityApi.fetchActivitiesByUserCount(params.uid)
    const user = await userApi.fetchUser(params.uid)
    mainContent.replaceChildren(
        H1(styles.HEADER, `Activities for ${user.name}`),
        ActivityList(activities),
        Pagination(activityCount, (skip, limit) => onPaginationChange(`users/${params.uid}/activities`, query, skip, limit))
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
    displayActivitiesBySport,
    displayActivitiesByUser,
    displayActivityDetails,
    displayActivityList,
}
