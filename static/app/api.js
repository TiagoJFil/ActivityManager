
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

async function fetchSport(id){
    return await fetchResource(id, SPORTS_URL)
}
async function fetchRoute(id){
    return await fetchResource(id, ROUTE_URL)
}
async function fetchUser(id){
    return await fetchResource(id, USERS_URL)
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


const api = {
    fetchSports,
    fetchRoutes,
    fetchUsers,
    fetchSport,
    fetchRoute,
    fetchUser
}

export default api  