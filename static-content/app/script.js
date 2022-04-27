import router from './router.js'
import handlers from './handlers.js'

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

const sportButton = document.querySelector('#add-sport')
const userButton = document.querySelector('#add-user')
const routeButton = document.querySelector('#add-route')

sportButton.addEventListener('click', () => {
    const sportName = prompt('Enter sport name')
    const description = prompt('Enter sport description')
    //285e3eb5-72c2-4cc7-92d9-586af2aaa885
    fetch('/api/sports', { 
        method: 'POST', 
        body: JSON.stringify({ name: sportName, description }), 
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer TOKEN' }
    })
})

userButton.addEventListener('click', () => {
    const userName = prompt('Enter userName')
    const email = prompt('Enter email')

    fetch('/api/users', { 
        method: 'POST', 
        body: JSON.stringify({ name: userName, email }), 
        headers: { 'Content-Type': 'application/json' }
    })
})

routeButton.addEventListener('click', () => {
    const startLocation = prompt('Enter start location')
    const endLocation = prompt('Enter end location')
    const distance = prompt('distance')

    fetch('/api/routes', { 
        method: 'POST', 
        body: JSON.stringify({ startLocation, endLocation, distance }), 
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer TOKEN' }
    })
})

function loadHandler(){

    router.addRouteHandler('home', handlers.getHome)
    router.addRouteHandler('sports', handlers.getSports) 
    router.addRouteHandler('sports/:sid', handlers.getSport) 
    router.addRouteHandler('users', handlers.getUsers)  
    router.addRouteHandler('users/:uid', handlers.getUser) 
    router.addRouteHandler('routes', handlers.getRoutes)  
    router.addRouteHandler('routes/:rid', handlers.getRoute)
    router.addRouteHandler('activities', handlers.getActivities)
    router.addRouteHandler('sports/:sid/activities', handlers.getActivitiesBySport)

   // router.addRouteHandler('sports/:sid/users', handlers.getUsers)  //TODO

   // router.addRouteHandler('users/:uid/activities', handlers.getActivitesFromUser)  //TODO
   // router.addRouteHandler('sports/:sid/activities/:aid', handlers.getSport)

    router.addDefaultNotFoundRouteHandler(handlers.getHome)

    hashChangeHandler()
}

function hashChangeHandler(){
    const mainContent = document.querySelector('#mainContent')
    const path = window.location.hash.replace('#', '')
    const handlerInfo = router.getRouteHandler(path)
    handlerInfo.handler(mainContent, handlerInfo.params, handlerInfo.query)

}
