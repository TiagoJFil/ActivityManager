import {List, Item, Text, Div, H1} from "../utils.js"

export default function RouteView(route) {
    const locationTextClass = 'location-text'

    return Div('header-div',
        H1('header', 'Route Details'),
        List('route',
            Div('route-display',
                Item('distance-item',
                    Text('distance-text', `Distance: ${route.distance} kilometers`)
                ),
                Div('locations',
                    Item('start-location-item',
                        Text(locationTextClass, `${route.startLocation}`)
                    ),
                    Div('locations-line'),
                    Item('end-location-item',
                        Text(locationTextClass, `${route.endLocation}`)
                    )
                )
            )
        )
    )
}
