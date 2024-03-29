import router from './router.js'
import handlers from './handlers/app-handlers.js'
import {Navigation} from './components/Navigation.js'
import {isLoggedIn} from './api/session.js'
import LoadingSpinner from "./components/LoadingSpinner.js";
import {isRendering, previousHash, renderContent, setIsRendering} from "./rendering.js";

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

/**
 * Loads the default routes
 */
function loadHandler() {
    if (!location.hash) location.hash = "#home"
    document.body.prepend(Navigation(isLoggedIn()))


    router.addDefaultNotFoundRouteHandler(handlers.getNotFoundPage)
    router.addRouteHandler('home', handlers.getHome)

    router.addRouteHandler('sports', handlers.getSports)
    router.addRouteHandler('sports/add', handlers.createSport)
    router.addRouteHandler('sports/:sid', handlers.getSport)
    router.addRouteHandler('sports/:sid/users', handlers.getUsersByActivity)  
    

    router.addRouteHandler('users/ranking', handlers.getUsersByRanking)  
    router.addRouteHandler('users/:uid', handlers.getUser) 
    
    router.addRouteHandler('routes', handlers.getRoutes)
    router.addRouteHandler('routes/add', handlers.createRoute)
    router.addRouteHandler('routes/:rid', handlers.getRoute)


    router.addRouteHandler('activities/search', handlers.getActivitiesSearch)
    router.addRouteHandler('sports/:sid/activities/add', handlers.createActivity)
    router.addRouteHandler('sports/:sid/activities/:aid', handlers.getActivity)
    router.addRouteHandler('sports/:sid/activities', handlers.getActivities)
    router.addRouteHandler('users/:uid/activities', handlers.getActivitiesByUser)

    router.addRouteHandler('login', handlers.getLogin)
    router.addRouteHandler('register', handlers.getRegister)
    router.addRouteHandler('logout', handlers.getLogout)

    hashChangeHandler()
}


/**
 * Its called everytime the hash changes.
 * Gets the route handler associated to the path after the hash.
 */
async function hashChangeHandler() {
    if (isRendering()) {
        window.location.hash = previousHash()
        return;
    }
    setIsRendering(true)

    const mainContent = document.querySelector('#mainContent')
    const path = window.location.hash.replace('#', '')
    const handlerInfo = router.getRouteHandler(path)

    try {
        renderContent(mainContent, LoadingSpinner())
        const content = await handlerInfo.handler(handlerInfo.params, handlerInfo.query)
        renderContent(mainContent, content)
    } catch (e) {
        console.log(e)
        handlers.getErrorPage(e)
    } finally {
        setIsRendering(false)
    }
}

