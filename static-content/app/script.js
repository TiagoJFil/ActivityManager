import router from './router.js'
import handlers from './handlers.js'

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)
window.addEventListener('resize', hashChangeHandler)

function loadHandler(){

    router.addDefaultNotFoundRouteHandler(handlers.getNotFoundPage)

    router.addRouteHandler('home', handlers.getHome)
    router.addRouteHandler('sports', handlers.getSports) 
    router.addRouteHandler('sports/:sid', handlers.getSport) 
    router.addRouteHandler('users', handlers.getUsers)  
    router.addRouteHandler('users/:uid', handlers.getUser) 
    router.addRouteHandler('routes', handlers.getRoutes)  
    router.addRouteHandler('routes/:rid', handlers.getRoute)
    router.addRouteHandler('activities', handlers.getActivities)
    router.addRouteHandler('sports/:sid/activities/:aid', handlers.getActivity)
    router.addRouteHandler('sports/:sid/users', handlers.getUsersByActivity)
    router.addRouteHandler('sports/:sid/activities', handlers.getActivitiesBySport)
    router.addRouteHandler('users/:uid/activities', handlers.getActivitiesByUser)

    hashChangeHandler()
}

function hashChangeHandler(){
    const mainContent = document.querySelector('#mainContent')
    const path = window.location.hash.replace('#', '')
    const handlerInfo = router.getRouteHandler(path)

    handlerInfo.handler(mainContent, handlerInfo.params, handlerInfo.query)
}
