import api from './api.js'
import SportList from './views/lists/SportList.js'
import UserList from './views/lists/UserList.js'
import RouteList from './views/lists/RouteList.js'
import SportView from './views/SportView.js'
import UserView from './views/UserView.js'
import RouteView from './views/RouteView.js'

function getHome(mainContent){
    const h1 = document.createElement('h1');
    const text = document.createTextNode("Home")
    h1.append(text)
    mainContent.replaceChildren(h1)
}

async function getSports(mainContent, params, query){
    const sports = await api.fetchSports(query)

    mainContent.replaceChildren(
        SportList(sports)
    )
}

async function getUsers(mainContent, params, query){
    const users = await api.fetchUsers(query)

    mainContent.replaceChildren(
        UserList(users)
    )
}

async function getRoutes(mainContent, params, query){
    const routes = await api.fetchRoutes(query)

    mainContent.replaceChildren(
        RouteList(routes)
    )
}

async function getRoute(mainContent, params, query){
    const route = await api.fetchRoute(params.rid)
    
    mainContent.replaceChildren(
        RouteView(route)
    )
}


async function getUser(mainContent, params, query){

    const user = await api.fetchUser(params.uid)

    mainContent.replaceChildren(
        UserView(user)
    )

}

async function getSport(mainContent, params, query){

    const sport = await api.fetchSport(params.sid)

    mainContent.replaceChildren(
        SportView(sport)
    )

}

const handlers = { 
    getHome,
    getSports,
    getSport,
    getUsers,
    getUser,
    getRoutes,
    getRoute
}

export default handlers
