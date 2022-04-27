import api from './api.js'
import SportList from './views/lists/SportList.js'
import UserList from './views/lists/UserList.js'
import RouteList from './views/lists/RouteList.js'
import ActivityList from './views/lists/ActivityList.js'
import SportView from './views/SportView.js'
import UserView from './views/UserView.js'
import RouteView from './views/RouteView.js'
import ActivityView from './views/ActivityView.js'
import {getItemsPerPage, Pagination} from "./views/Pagination.js";

function getHome(mainContent){
    const h1 = document.createElement('h1');
    const text = document.createTextNode("Home")
    h1.append(text)
    mainContent.replaceChildren(h1)
}

function getQuery(){
    const limit = getItemsPerPage()
    const pageLinkActive = document.querySelector('.page-link-active')
    console.log(pageLinkActive)
    const skip = pageLinkActive ? parseInt(pageLinkActive.firstChild.textContent) * limit : 0
    console.log(skip)
    return `?skip=${skip}&limit=${limit}`
}

async function getSports(mainContent, params, query){
    const sports = await api.fetchSports(getQuery())
    const sportCount = await api.fetchSportsCount()

    mainContent.replaceChildren(
        SportList(sports),
        Pagination('sports',sportCount)
    )
}

async function getSport(mainContent, params, query){
    const sport = await api.fetchSport(params.sid)

    mainContent.replaceChildren(
        SportView(sport)
    )
}

async function getUsers(mainContent, params, query){
    const users = await api.fetchUsers(getQuery())
    const userCount = await api.fetchUsersCount()

    mainContent.replaceChildren(
        UserList(users),
        Pagination('users', userCount)
    )
}

async function getUser(mainContent, params, query){
    const user = await api.fetchUser(params.uid)

    mainContent.replaceChildren(
        UserView(user)
    )
}

async function getRoutes(mainContent, params, query){
    const routes = await api.fetchRoutes(getQuery())
    const routeCount = await api.fetchRoutesCount()
    mainContent.replaceChildren(
        RouteList(routes),
        Pagination('routes', routeCount)
    )
}

async function getRoute(mainContent, params, query){
    const route = await api.fetchRoute(params.rid)
    mainContent.replaceChildren(
        RouteView(route)
    )
}


async function getActivities(mainContent, params, query){
    const activities = await api.fetchActivities(getQuery())
    //const actvWithSportName = await api.addSportNameToActivities(activities)
    const activityCount = await api.fetchActivitiesCount()
    mainContent.replaceChildren(
        ActivityList(activities, 'All Activities'),
        Pagination('activities', activityCount)
    )
}

async function getActivitiesBySport(mainContent, params, query){
    const activities = await api.fetchActivitiesBySport(params.sid, getQuery())
    const actvWithSportName = await api.addSportNameToActivities(activities)

   // const activityCount = await api.fetchResourceCount('activities')
//TODO: add pagination count here
    mainContent.replaceChildren(
        ActivityList(actvWithSportName, `Activities for ${actvWithSportName.sportName}`),
        Pagination('activities',9)
    )
}

async function getActivitiesByUser(mainContent, params, query){
    const activities = await api.fetchActivitiesByUser(params.uid, getQuery())
    mainContent.replaceChildren(
        ActivityList(activities, `Activities for this USER`),
        Pagination('activities')
    )
}

async function getActivity(mainContent, params, query){
    const activity = await api.fetchActivity(params.sid,params.aid)

    mainContent.replaceChildren(
        ActivityView(activity)
    )
}

async function getUsersByActivity(mainContent, params, query){
    const users = await api.fetchUsersByActivity(getQuery(),params.sid)

    mainContent.replaceChildren(
        UserList(users),
        Pagination('users', userCount)
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
    getActivitiesBySport
}


