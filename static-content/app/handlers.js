import api from './api.js'
import SportList from './views/lists/SportList.js'
import UserList from './views/lists/UserList.js'
import RouteList from './views/lists/RouteList.js'
import ActivityList from './views/lists/ActivityList.js'
import SportView from './views/individualResources/SportView.js'
import UserView from './views/individualResources/UserView.js'
import RouteView from './views/individualResources/RouteView.js'
import ActivityView from './views/individualResources/ActivityView.js'
import {getItemsPerPage, Pagination, getPaginationQuery} from "./views/Pagination.js"
import {Div, H1, Text} from "./views/utils.js"
import ActivitySearchFilter from "./views/ActivitySearchFilter.js"
import styles from "./styles.js";

function getHome(mainContent) {
    const h1 = H1(styles.HEADER, 'Home')
    mainContent.replaceChildren(h1)
}


function queryBuilder(queryValues){
    if(queryValues === undefined) return ''

    return Object
        .keys(queryValues)
        .map(key => `${key}=${queryValues[key]}`)
        .join('&')
}

function onPaginationChange(path, currentQuery, skip,limit){

    const query = currentQuery ? currentQuery : {}

    query.skip = skip
    query.limit = limit
    
    window.location.hash = `#${path}?${queryBuilder(query)}`
}

async function getSports(mainContent, params, query) {
    const paginationQuery = queryBuilder(query)
    const sports = await api.fetchSports(paginationQuery)
    const sportCount = await api.fetchSportsCount()
    

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Sports'),
        SportList(sports),
        Pagination(sportCount, (skip, limit) => onPaginationChange("sports", query, skip, limit))
    )
}


async function getSport(mainContent, params, query) {
    const sport = await api.fetchSport(params.sid)

    mainContent.replaceChildren(
        SportView(sport)
    )
}

async function getUsers(mainContent, params, query) {
    const users = await api.fetchUsers(queryBuilder(query) || getPaginationQuery())
    const userCount = await api.fetchUsersCount()

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Users'),
        UserList(users),
        Pagination(userCount, (skip, limit) => onPaginationChange("users", query, skip, limit))
    )
}

async function getUser(mainContent, params, query) {
    const user = await api.fetchUser(params.uid)

    mainContent.replaceChildren(
        UserView(user)
    )
}

async function getRoutes(mainContent, params, query) {
    const routes = await api.fetchRoutes(queryBuilder(query) || getPaginationQuery())
    const routeCount = await api.fetchRoutesCount()
    
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Routes'),
        RouteList(routes),
        Pagination(routeCount, (skip, limit) => onPaginationChange("routes", query, skip, limit))
    )
}

async function getRoute(mainContent, params, query) {
    const route = await api.fetchRoute(params.rid)
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Route Details'),
        RouteView(route),
        Div('spacer', '')
    )
}


async function getActivities(mainContent, params, query) {
    const activities = await api.fetchActivities(queryBuilder(query) || getPaginationQuery())
    const activityCount = await api.fetchActivitiesCount()

    mainContent.replaceChildren(
        H1(styles.HEADER, 'All Activities'),
        ActivityList(activities),
        Pagination(activityCount, (skip, limit) => onPaginationChange("activities", query, skip, limit))
    )
}

async function getActivitiesBySport(mainContent, params, query) {
    const activities = await api.fetchActivitiesBySport(params.sid, queryBuilder(query) || getPaginationQuery())
    const queryForCount = {...query}
    if(queryForCount){
        queryForCount.skip = 0
        queryForCount.limit = 1000000
    }
    
    const activitiesCount = await api.fetchActivitiesBySportCount(params.sid, queryBuilder(queryForCount)) // TODO: Passar a query para a request
    console.log(activitiesCount)
    const routes = await api.fetchRoutes(`limit=1000000`)
    const sport = await api.fetchSport(params.sid)
  
    const onFilterSubmit =  (date, route, sortOrder) => {
        const newQuery = query ? query : {}
        if(date) newQuery.date = date
        if(route && newQuery.rid != route){
            newQuery.rid = route
            newQuery.limit = 10
            newQuery.skip = 0
        }
        if(sortOrder) newQuery.orderBy = sortOrder
        
        window.location.hash = `#sports/${params.sid}/activities?${queryBuilder(newQuery)}`
    }
    console.log(activitiesCount)
    mainContent.replaceChildren(
        Div('activity-header-filter',
            H1(styles.HEADER, `Activities for ${sport.name}`),
            ActivitySearchFilter(onFilterSubmit, routes),
        ),
        ActivityList(activities),
        Pagination(activitiesCount, (skip, limit) => onPaginationChange(`sports/${params.sid}/activities`, query, skip, limit))
    )
}

async function getActivitiesByUser(mainContent, params, query) {
    const activities = await api.fetchActivitiesByUser(params.uid, queryBuilder(query) || getPaginationQuery())
    const activityCount = await api.fetchActivitiesByUserCount(params.uid)
    const user = await api.fetchUser(params.uid)
    mainContent.replaceChildren(
        H1(styles.HEADER, `Activities for ${user.name}`),
        ActivityList(activities),
        Pagination(activityCount, (skip, limit) => onPaginationChange(`users/${params.uid}/activities`, query, skip, limit))
    )
}

async function getActivity(mainContent, params, query) {
    const activity = await api.fetchActivity(params.sid, params.aid)

    mainContent.replaceChildren(
        ActivityView(activity)
    )
}

async function getUsersByActivity(mainContent, params, query) {
    const users = await api.fetchUsersByActivity(queryBuilder(query) || getPaginationQuery(), params.sid)
    const userCount = await api.fetchUserByActivityCount(queryBuilder(query), params.sid)
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Users'),
        UserList(users),
        Pagination(userCount, (skip, limit) => onPaginationChange(`sports/${activities.sports}/users`, query, skip, limit))
    )
}


function getNotFoundPage(mainContent, params, query) {

    mainContent.replaceChildren(
        Div('not-found',
            H1(styles.HEADER, '404-Not Found'),
            Text(styles.TEXT,'Sorry, the page you are looking for does not exist.\n'),
            Text(styles.TEXT,'Try heading to the home page.')
        )
    )
}



export default {
    getHome,
    getSports,
    getSport,
    getUsers,
    getUser,
    getRoutes,
    getRoute,
    getActivities,
    getActivity,
    getUsersByActivity,
    getActivitiesByUser,
    getActivitiesBySport,
    getNotFoundPage
}


