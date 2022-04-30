
const BASE_API_URL = 'http://localhost:9000/api/';
const SPORTS_URL = 'sports'
const USERS_URL = 'users'
const ROUTE_URL = 'routes'
const ACTIVITIES_URL = 'activities'
const ACTIVITY_URL = sid => `sports/${sid}/activities`
const ACTIVITY_SPORT_URL = sid => `sports/${sid}/activities`
const ACTIVITY_USER_URL = uid => `users/${uid}/activities`

async function fetchSports(query) {
    return await fetchResourceList(query, SPORTS_URL)   
}

async function fetchRoutes(query){
   return await fetchResourceList(query, ROUTE_URL)
}

async function fetchUsers(query){
    return await fetchResourceList(query, USERS_URL) 
}

async function fetchActivities(query){
    return await fetchResourceList(query, ACTIVITIES_URL)
}

async function fetchActivitiesBySport(sid, query){
    const response = await fetch(BASE_API_URL + ACTIVITY_SPORT_URL(sid) + (query ? '?' + query : ''))
    const activities = await response.json()
    return activities['activities']
}

async function fetchActivitiesBySportCount(sid,query){
    const response = await fetchActivitiesBySport(sid,query)
    return response.length
}

async function fetchActivitiesByUser(uid, query){
    const response = await fetch(BASE_API_URL + ACTIVITY_USER_URL(uid) + (query ? '?' + query : ''))
    const activities = await response.json()
    return activities['activities']
}

async function fetchActivitiesByUserCount(uid){
    const response = await fetchActivitiesByUser(uid, `limit=1000000`)
    return response.length
}

async function fetchSportsCount(){
    return await fetchResourceCount(SPORTS_URL)
}

async function fetchRoutesCount(){
    return await fetchResourceCount(ROUTE_URL)
}

async function fetchUsersCount(){
    return await fetchResourceCount(USERS_URL)
}

async function fetchActivitiesCount(){
    return await fetchResourceCount(ACTIVITIES_URL)
}

async function fetchSport(id){
    return await fetchResource(id, SPORTS_URL)
}
async function fetchRoute(id){
    return await fetchResource(id, ROUTE_URL)
}
async function fetchUser(id){
    return await fetchResource(id, USERS_URL)
}

async function fetchActivity(sid, id){
    return await fetchResource(id, ACTIVITY_URL(sid))
}

async function fetchResourceList(query, RESOURCE_PATH){
    const response = await fetch(BASE_API_URL + RESOURCE_PATH + (query ? '?' + query : ''))
    const object = await response.json()
    return object[RESOURCE_PATH]
}

async function fetchResourceCount(RESOURCE_PATH){
    const resources = await fetchResourceList(`limit=1000000`,RESOURCE_PATH)

    return resources.length
}

async function fetchResource(id, RESOURCE_PATH){
    const response = await fetch(BASE_API_URL + RESOURCE_PATH + '/' + id)
    const object = await response.json()

    if(Math.floor(response.status / 100) === 2) 
        return object
    else{
        throw object
    }
}

async function fetchUsersByActivity(query,sid){
    const response = await fetch(BASE_API_URL + 'sports/' + sid + '/users' + (query ? '?' + query : ''))
    const userList = await response.json()
    return userList['users']
}

async function fetchUserByActivityCount(query, sid){
    const ridQuery = query.split('&').find(element => element.includes('rid')) 
    const users = await fetchUsersByActivity(ridQuery + `&limit=1000000`, sid)
    return users.length
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

