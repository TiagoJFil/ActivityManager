import api from './api.js'
import SportList from './views/lists/SportList.js'
import UserList from './views/lists/UserList.js'
import RouteList from './views/lists/RouteList.js'
import ActivityList from './views/lists/ActivityList.js'
import SportView from './views/SportView.js'
import UserView from './views/UserView.js'
import RouteView from './views/RouteView.js'
//import ActivityView from './views/ActivityView.js'
import {Pagination} from "./views/utils.js";

function getHome(mainContent){
    const h1 = document.createElement('h1');
    const text = document.createTextNode("Home")
    h1.append(text)
    mainContent.replaceChildren(h1)
}

async function getSports(mainContent, params, query){
    const sports = await api.fetchSports(query)
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
    const users = await api.fetchUsers(query)
    const userCount = await api.fetchUsersCount()

    mainContent.replaceChildren(
        UserList(users),
        Pagination('users',userCount)
    )
}

async function getUser(mainContent, params, query){
    const user = await api.fetchUser(params.uid)

    mainContent.replaceChildren(
        UserView(user)
    )
}

async function getRoutes(mainContent, params, query){
    console.log(query)
    const routes = await api.fetchRoutes(query)
    const routeCount = await api.fetchRoutesCount()
    mainContent.replaceChildren(
        RouteList(routes),
        Pagination('routes',routeCount)
    )
}

async function getRoute(mainContent, params, query){
    const route = await api.fetchRoute(params.rid)
    mainContent.replaceChildren(
        RouteView(route)
    )
}


async function getActivities(mainContent, params, query){
    const activities = await api.fetchActivities(query)
    const activityCount = await api.fetchResourceCount('activities')
    mainContent.replaceChildren(
        ActivityList(activities),
        Pagination('activity',activityCount)
    )
}

async function getActivitiesBySport(mainContent, params, query){
    const activities = await api.fetchActivitiesBySport(params.sid, query)
    const activityCount = await api.fetchResourceCount('activities')
    mainContent.replaceChildren(
        ActivityList(activities),
        Pagination('activity',activityCount)
    )
}

async function getActivity(mainContent, params, query){
    const activity = await api.fetchActivity(params.aid)

    mainContent.replaceChildren(
        ActivityView(activity)
    )
}

const handlers = {
    getHome,
    getSports,
    getSport,
    getUsers,
    getUser,
    getRoutes,
    getRoute,
    getActivities,
    getActivitiesBySport,
    getActivity
}

export default handlers
