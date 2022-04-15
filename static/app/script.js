import router from './router.js'
import handlers from './handlers.js'

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){

    router.addRouteHandler('home', handlers.getHome)
    router.addRouteHandler('sports', handlers.getSports)
    router.addRouteHandler('sports/:id/activities/:aid', handlers.getSport)

    hashChangeHandler()
}

function hashChangeHandler(){
    const mainContent = document.querySelector('#mainContent')
    const path = window.location.hash.replace('#', '')

    const handler = router.getRouteHandler(path)
    handler(mainContent)
}
