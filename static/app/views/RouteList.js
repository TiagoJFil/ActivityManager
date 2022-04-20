import { List, Item } from './utils.js'

/**
 * RouteList component
 * 
 * @param {*} routes routes to display
 * @returns An "ul" element containing a list of "li" elements for each route
 */
export default function RoutesList(routes){
    return List('route-list',
        ...routes.map(route =>
            Item('route',
                `${route.startLocation} - ${route.endLocation}`.linkTo(`#routes/${route.id}`)
            )
        )
    )
}