import ResourceList from './ResourceList.js';

/**
 * RouteList component
 *
 * @param {*} routes routes to display
 * @returns An "ul" element containing a list of "li" elements for each route
 */
export default function RoutesList(routes) {
    return ResourceList( 
        routes,
        (route) => `#routes/${route.id}`,
        (route) => {
            const MAX_LENGTH = 20;
            const display = `${route.startLocation} - ${route.endLocation}`
            if(display.length > MAX_LENGTH){
                return display.substring(0, MAX_LENGTH) + '...'
            }else 
                return display
        }
    )
}