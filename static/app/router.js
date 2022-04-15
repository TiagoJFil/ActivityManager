
const routes = [
    // { pathRegex: "", handler: () => {} }
]

let notFoundRouteHandler = mainContent => console.log("Not Found")

function addRouteHandler(path, handler){
    // Path: sports/:id
    const pathRegex = new RegExp(path.replace(/:[^/]+/g, "([^/]+)").replace(/\//g, "\\/") + "$")
    console.log(pathRegex)
    routes.push({pathRegex, handler})
}

function getRouteHandler(path){// Find the route that matches the path

    const route = routes.find(route => route.pathRegex.test(path))

    if(!route) return notFoundRouteHandler

    console.log(route.pathRegex.exec(path))

    return route.handler
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