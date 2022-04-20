import api from './api.js'
import SportList from './views/SportList.js';
import UserList from './views/UserList.js';
import RouteList from './views/RouteList.js';

function getHome(mainContent){
    const h1 = document.createElement('h1');
    const text = document.createTextNode("Home")
    h1.append(text);
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

async function getResources(mainContent, query, supplier, displayElement){
    const resources = await supplier(query)
    
    mainContent.replaceChildren(
        List(
            ...resources.map((resource) => Item(displayElement(resource)))
        )
    )
}

async function getSport(mainContent, params, query){
    const sport = await api.fetchASport(params.sid)
    const ul = document.createElement('ul')
    for (let [key, value] of Object.entries(sport)){
        const li = document.createElement('li')
        if(key != 'user') {
            
            const text = document.createTextNode(key + ': ' + value)
            li.append(text)
        }else {
            const a = document.createElement('a')
            const keyElem = document.createTextNode("user: ")
            
            const text = document.createTextNode("User")
            li.append(keyElem)
            a.href = `#users/${value}`
            a.append(text)
            li.append(a)
        }
        
        ul.append(li)
    }
    mainContent.replaceChildren(ul)
}

const handlers = { 
    getHome,
    getSports,
    getSport,
    getUsers,
    getRoutes
}

export default handlers
