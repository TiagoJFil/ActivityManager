import {fetchResourceList, getRequest, getBodyOrThrow, isSuccessful, sendRequest} from "./api-utils.js";

// URL Related Constants

const BASE_API_URL = '/api/';
const SPORTS_URL = 'sports'
const USERS_URL = 'users'
const ROUTE_URL = 'routes'
const ACTIVITIES_URL = 'activities'

const ACTIVITY_SPORT_URL = sid => `sports/${sid}/activities`
const ACTIVITY_USER_URL = uid => `users/${uid}/activities`

// List Property names
const ROUTES_PROPERTY = 'routes'
const SPORTS_PROPERTY = 'sports'
const USERS_PROPERTY = 'users'
const ACTIVITIES_PROPERTY = 'activities'



/**
 * Makes a post request to the API to create a sport
 */
async function createSport(sportName, sportDescription) {
    const uri = BASE_API_URL + SPORTS_URL 
    const body = JSON.stringify({
        name: sportName,
        description: sportDescription
    })
    const response = await sendRequest(uri, 'POST',body)

    return await getBodyOrThrow(response)
}


/**
 * Makes a post request to the API to create a route.
 */
async function createRoute(sLocation, eLocation, distance) {
    const uri = BASE_API_URL + ROUTE_URL
    const body = JSON.stringify({
        startLocation: sLocation || '',
            endLocation: eLocation || '',
            distance: distance ? parseFloat(distance) : ""
    })
    const response = await sendRequest(uri, 'POST',body)
    
    return await getBodyOrThrow(response)
}

/**
 * Makes a post request to the API to create an activity
 */
async function createActivity(sid, date, duration, rid) {
    const uri = BASE_API_URL + ACTIVITY_SPORT_URL(sid)
    const body = JSON.stringify({
        duration: duration,
        date: date,
        rid: rid || ''
    })
    const response = await sendRequest(uri, 'POST',body)
    
    return await getBodyOrThrow(response)
}


/**
 * Makes a put request to the API to update a sport
 */
 async function updateSport(sid, sportName, sportDescription) {
    const uri = BASE_API_URL + SPORTS_URL + '/' + sid
    const body = JSON.stringify({
        "name": sportName,
        "description": sportDescription || ''
    })
    const response = await sendRequest(uri, 'PUT',body)

    if(!isSuccessful(response.status))
        throw await response.json()    
}


/**
 * Makes a put request to the API to update a route
 */
async function updateRoute(rid, sLocation, eLocation, distance) {
    const uri = BASE_API_URL + ROUTE_URL + '/' + rid
    const body = JSON.stringify({
        startLocation: sLocation || '',
        endLocation: eLocation || '',
        distance: distance ? parseFloat(distance) : ""
    })
    const response = await sendRequest(uri, 'PUT',body)

    if(!isSuccessful(response.status))
        throw await response.json()
}


/**
 * Makes a put request to the API to update an activity
 */
async function updateActivity(sid,aid,date, duration, rid) {
    const uri = BASE_API_URL + ACTIVITY_SPORT_URL(sid) + '/' + aid
    const body = JSON.stringify({
        date: date,
        duration: duration,
        rid: rid || ''
    })
    const response = await sendRequest(uri, 'PUT',body)

    if(!isSuccessful(response.status))
        throw await response.json()
}

/**
 * Makes a delete request to the API to delete an activity
 */
async function deleteActivity(sid,aid){
    const uri = BASE_API_URL + ACTIVITY_SPORT_URL(sid) + '/' + aid
    const response = await sendRequest(uri, 'Delete', null)

    if(!isSuccessful(response.status))
        throw await response.json()
}


export const userApi = {

    fetchUsers: async (queryMap) =>
        await fetchResourceList(BASE_API_URL + USERS_URL, queryMap, USERS_PROPERTY),

    fetchUser: async (uid) =>
        await getRequest(`${BASE_API_URL}${USERS_URL}/${uid}`, null),

    fetchUsersByActivity: async (sid, queryObject) =>
        await fetchResourceList(BASE_API_URL + `${SPORTS_URL}/${sid}/users`, queryObject, USERS_PROPERTY),

}

export const sportApi = {

    fetchSport: async (sid) =>
        await getRequest(`${BASE_API_URL}${SPORTS_URL}/${sid}`, null),

    fetchSports: async (queryMap) =>
        await fetchResourceList(BASE_API_URL + SPORTS_URL, queryMap, SPORTS_PROPERTY),

    createSport,
    updateSport
}

export const activityApi = {

    fetchActivities: async (queryMap) =>
        await fetchResourceList(BASE_API_URL + ACTIVITIES_URL, queryMap, ACTIVITIES_PROPERTY),

    fetchActivitiesBySport: async (sid, queryMap) =>
        await fetchResourceList(BASE_API_URL + ACTIVITY_SPORT_URL(sid), queryMap, ACTIVITIES_PROPERTY),

    fetchActivitiesByUser: async (uid, queryMap) =>
        await fetchResourceList(BASE_API_URL + ACTIVITY_USER_URL(uid), queryMap, ACTIVITIES_PROPERTY),

    fetchActivity: async (sid, aid) =>
        await getRequest(`${BASE_API_URL}${ACTIVITY_SPORT_URL(sid)}/${aid}`, null),

    deleteActivity,
    updateActivity,
    createActivity

}

export const routeApi = {
    fetchRoute: async (rid) =>
        await getRequest(`${BASE_API_URL}${ROUTE_URL}/${rid}`, null),

    fetchRoutes: async (queryMap) =>
        await fetchResourceList(BASE_API_URL + ROUTE_URL, queryMap, ROUTES_PROPERTY),

    createRoute,
    updateRoute 
}

