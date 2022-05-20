
const routes = [

]

let notFoundRouteHandler = (mainContent,params) => getNotFoundPage(mainContent,params)

/**
 * Associates the uri with the given function
 */
function addRouteHandler(path, handler){

    const pathRegex = new RegExp('^' + path.replace(/:[^/]+/g, "([0-9a-z]+)").replace(/\//g, "\\/") + "\??$")
    const placeholderNames = path.split("/")
    .filter(p => p.startsWith(":"))
    .map(p => p.replace(":", ""))
    // [sid, aid]
    routes.push({pathRegex, handler, placeholderNames})
}

/**
 * Returns the route handler for the given uri
 */
export function getRouteHandler(path){
    // sports/39/activities/27
    const [pathString, queryString] = path.split("?")

    const route = routes.find(route => route.pathRegex.test(pathString))

    if(!route) return { handler: notFoundRouteHandler , params: {}, query: {}}

    const [match, ...values] = route.pathRegex.exec(pathString)
    const params = {}
    route.placeholderNames.forEach((name, idx) => params[name] = values[idx])
    console.log(route)
    // params: {sid: 39, aid: 27}
    return {handler:route.handler , params, query: queryString ? parseQuery(queryString) : {}}
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

/**
 * Transforms the given query string into an object 
 */
function parseQuery(queryString){
    const query = {}
    const queryParts = queryString.split("&")
    queryParts.forEach(part => {
        const [key, value] = part.split("=")
        query[key] = value
    })

    return query
}

const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler
}

export default router