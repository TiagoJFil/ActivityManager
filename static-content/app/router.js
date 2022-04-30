
const routes = [

]

let notFoundRouteHandler = (mainContent,params) => getNotFoundPage(mainContent,params)

function addRouteHandler(path, handler){

    const pathRegex = new RegExp('^' + path.replace(/:[^/]+/g, "([0-9a-z]+)").replace(/\//g, "\\/") + "\??$")
    const placeholderNames = path.split("/")
    .filter(p => p.startsWith(":"))
    .map(p => p.replace(":", ""))
    // [sid, aid]
    routes.push({pathRegex, handler, placeholderNames})
}

export function getRouteHandler(path){
    // sports/39/activities/27
    const [pathString, queryString] = path.split("?")

    const route = routes.find(route => route.pathRegex.test(pathString))
    if(!route) return { handler: notFoundRouteHandler , params: {}, query: {}}

    const [match, ...values] = route.pathRegex.exec(pathString)
    const params = {}
    route.placeholderNames.forEach((name, idx) => params[name] = values[idx])

    // params: {sid: 39, aid: 27}
    return {handler:route.handler , params, query: queryString ? parseQuery(queryString) : {}}
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

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