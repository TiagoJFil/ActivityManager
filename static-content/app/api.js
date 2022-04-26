
const BASE_API_URL = 'http://localhost:9000/api/';
const SPORTS_URL = 'sports'
const USERS_URL = 'users'
const ROUTE_URL = 'routes'
const ACTIVITY_URL = sid => `sports/${sid}/activities`

async function fetchSports(query) {
    return  await fetchResourceList(query, SPORTS_URL)   
}

async function fetchRoutes(query){
   return await fetchResourceList(query, ROUTE_URL)
}

async function fetchUsers(query){
    return await fetchResourceList(query, USERS_URL) 
}

async function fetchActivities(sid, query){
    //TODO make this
    TODO
    const sports = await fetchSports(query)
    return await fetchResourceList(query, ACTIVITY_URL(sid))
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

async function fetchResource(id, RESOURCE_PATH){
    const response = await fetch(BASE_API_URL + RESOURCE_PATH + '/' + id)
    const object = await response.json()
    return object
}

async function fetchResourceCount(RESOURCE_PATH){
    const resources = await fetchResourceList('limit=10000000',RESOURCE_PATH)
    return resources.length
}

const api = {
    fetchSports,
    fetchRoutes,
    fetchUsers,
    fetchSport,
    fetchRoute,
    fetchUser,
    fetchActivities,
    fetchActivity,
    fetchSportsCount,
    fetchRoutesCount,
    fetchUsersCount
}

export default api  