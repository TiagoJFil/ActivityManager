import {List, Item, Text, Div, Icon, Anchor,UserLink} from "./utils.js"

export default function RouteView(route){
    const locationTextClass = 'location-text'
   
    return List('route',
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
            ),
            Item('user-link-item',
                UserLink('Created by: ','this user',route.user)
            )
        )
    )
}
