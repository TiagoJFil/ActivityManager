import { List, Item, Text ,Anchor} from '../utils.js'

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
                Anchor('link', `#routes/${route.id}`,
                        Text("text",
                            `${route.startLocation} - ${route.endLocation}`
                        )
                )
            )
        )
    )
}