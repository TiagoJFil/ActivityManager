const BASE_API_URL = 'http://localhost:9000/api/';
const SPORTS_URL = 'sports'
const USERS_URL = 'users'
const ROUTE_URL = 'routes'
const ACTIVITIES_URL = 'activities'
const ACTIVITY_URL = sid => `sports/${sid}/activities`
const ACTIVITY_SPORT_URL = sid => `sports/${sid}/activities`
const ACTIVITY_USER_URL = uid => `users/${uid}/activities`
const LIMIT_QUERY = 'limit=1000000'

/**
 * Fetches a list of sports from the API
 * @returns
 */
async function fetchSports(query) {
    return await fetchResourceList(query, SPORTS_URL)
}

/**
 * Fetches a list of routes from the API
 */
async function fetchRoutes(query) {
    return await fetchResourceList(query, ROUTE_URL)
}

/**
 * Fetches a list of users from the API
 */
async function fetchUsers(query) {
    return await fetchResourceList(query, USERS_URL)
}

/**
 * Fetches a list of activities from the API
 */
async function fetchActivities(query) {
    return await fetchResourceList(query, ACTIVITIES_URL)
}

/**
 * Fetches a list of activities associated with the given sport from the API
 */
async function fetchActivitiesBySport(sid, query) {
    const response = await fetch(BASE_API_URL + ACTIVITY_SPORT_URL(sid) + (query ? '?' + query : ''))
    const activities = await response.json()
    return activities['activities']
}

/**
 * Fetches the count of a list of activities associated with the given sport from the API
 */
async function fetchActivitiesBySportCount(sid, query) {
    const response = await fetchActivitiesBySport(sid, query)
    return response.length
}

/**
 * Fetches a list of activities associated with the given user from the API
 */
async function fetchActivitiesByUser(uid, query) {
    const response = await fetch(BASE_API_URL + ACTIVITY_USER_URL(uid) + (query ? '?' + query : ''))
    const activities = await response.json()
    return activities['activities']
}

/**
 * Fetches the count of a list of activities associated with the given user  from the API
 */
async function fetchActivitiesByUserCount(uid) {
    const response = await fetchActivitiesByUser(uid, LIMIT_QUERY)
    return response.length
}

/**
 * Fetch the count of existing sports in the database
 */
async function fetchSportsCount(query) {
    return await fetchResourceCount(SPORTS_URL, query)
}

/**
 * Fetch the count of existing routes in the database
 */
async function fetchRoutesCount(query) {
    return await fetchResourceCount(ROUTE_URL, query)
}

/**
 * Fetch the count of existing users in the database
 */
async function fetchUsersCount() {
    return await fetchResourceCount(USERS_URL, query)
}

/**
 * Fetch the count of existing activities in the database
 */
async function fetchActivitiesCount() {
    return await fetchResourceCount(ACTIVITIES_URL, query)
}

/**
 * Fetch a sport with the given id
 */
async function fetchSport(id) {
    return await fetchResource(id, SPORTS_URL)
}

/**
 * Fetch a route with the given id
 */
async function fetchRoute(id) {
    return await fetchResource(id, ROUTE_URL)
}

/**
 * Fetch a user with the given id
 */
async function fetchUser(id) {
    return await fetchResource(id, USERS_URL)
}

/**
 * Fetch an activity with the given id associated with the given sid from the API
 */
async function fetchActivity(sid, id) {
    return await fetchResource(id, ACTIVITY_URL(sid))
}

/**
 * Fetches a list of resources from the API
 */
async function fetchResourceList(query, RESOURCE_PATH) {
    const response = await fetch(BASE_API_URL + RESOURCE_PATH + (query ? '?' + query : ''))
    const object = await response.json()
    return object[RESOURCE_PATH]
}


/**
 * Fetches the count of existing resources in the database
 */
async function fetchResourceCount(RESOURCE_PATH, query) {
    const resources = await fetchResourceList((query ? '?' + query : ''), RESOURCE_PATH)
    return resources.length
}

/**
 * Fetches a resource from the API
 */
async function fetchResource(id, RESOURCE_PATH) {
    const response = await fetch(BASE_API_URL + RESOURCE_PATH + '/' + id)
    const object = await response.json()

    if (Math.floor(response.status / 100) === 2)
        return object
    else {
        throw object
    }
}


/**
 * Fetches a list of users associated with the given activity from the API
 */
async function fetchUsersByActivity(query, sid) {
    const response = await fetch(BASE_API_URL + 'sports/' + sid + '/users' + (query ? '?' + query : ''))
    const userList = await response.json()
    return userList['users']
}

/**
 * Fetches a list of users associated with the given activity from the API
 */
async function fetchUserByActivityCount(query, sid) {
    const ridQuery = query.split('&').find(element => element.includes('rid'))
    const users = await fetchUsersByActivity(ridQuery + `&${LIMIT_QUERY}`, sid)
    return users.length
}

/**
 * Makes a post request to the API to create a sport
 */
async function createSport(sportName, sportDescription) {
    await fetch(BASE_API_URL + SPORTS_URL, {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + 'TOKEN',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: sportName,
            description: sportDescription
        })
    })
}

export const userApi = {
    fetchUsers,
    fetchUsersCount,
    fetchUser,
    fetchUsersByActivity,
    fetchUserByActivityCount
}

export const sportApi = {
    fetchSport,
    fetchSports,
    fetchSportsCount,
    createSport
}

export const activityApi = {
    fetchActivities,
    fetchActivitiesCount,
    fetchActivitiesBySport,
    fetchActivitiesBySportCount,
    fetchActivitiesByUser,
    fetchActivitiesByUserCount,
    fetchActivity
}

export const routeApi = {
    fetchRoute,
    fetchRoutes,
    fetchRoutesCount
}

