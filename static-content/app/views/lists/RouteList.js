import {List, Item, Text, Div, H1, Button} from '../utils.js'

/**
 * RouteList component
 *
 * @param {*} routes routes to display
 * @returns An "ul" element containing a list of "li" elements for each route
 */
export default function RoutesList(routes) {
    return Div('header-div',
        H1('header', 'Routes'),
        List('list',
            ...routes.map(route =>
                Item('resource-route-item',
                    Button('list-button',
                        () => {
                            location.href = `#routes/${route.id}`
                        },
                        Text("text",
                            `${route.startLocation} - ${route.endLocation}`
                        )
                    )
                )
            )
        )
    )
}