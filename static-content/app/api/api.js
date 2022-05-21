import {fetchResourceList, getRequest} from "./api-utils.js";

// URL Related Constants

const BASE_API_URL = 'http://localhost:9000/api/';
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
    const response = await fetch(BASE_API_URL + SPORTS_URL, {
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

    if(Math.floor(response.status / 100) === 4) {
        throw (await response.json())
    }

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

    createSport

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

}

export const routeApi = {
    fetchRoute: async (rid) =>
        await getRequest(`${BASE_API_URL}${ROUTE_URL}/${rid}`, null),

    fetchRoutes: async (queryMap) =>
        await fetchResourceList(BASE_API_URL + ROUTE_URL, queryMap, ROUTES_PROPERTY),
}

