
const routes = { }

let notFoundRouteHandler = () => { throw "Route handler for unknown routes not defined" }

function addRouteHandler(path, handler){
    routes[path] = handler
}

function getRouteHandler(path){
    const handler = routes[path]
    return handler ? handler : notFoundHandler
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler
}

export default router