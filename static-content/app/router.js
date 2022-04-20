
const routes = [
    // { pathRegex: "", handler: () => {} }
]

let notFoundRouteHandler = mainContent => console.log("Not Found")

function addRouteHandler(path, handler){

    const pathRegex = new RegExp(path.replace(/:[^/]+/g, "([^/]+)").replace(/\//g, "\\/") + "\/?$")
    const placeholderNames = path.split("/")
    .filter(p => p.startsWith(":"))
    .map(p => p.replace(":", ""))
    // [sid, aid]
    routes.push({pathRegex, handler, placeholderNames})
}

function getRouteHandler(path){
    // sports/39/activities/27
    const [pathString, queryString] = path.split("?")
    const route = routes.find(route => route.pathRegex.test(pathString))

    if(!route) return notFoundRouteHandler
    const [match, ...values] = route.pathRegex.exec(pathString)
    
    const params = {}
    route.placeholderNames.forEach((name, idx) => params[name] = values[idx])
    // params: {sid: 39, aid: 27}
    return {handler: route.handler, params, query: queryString}
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