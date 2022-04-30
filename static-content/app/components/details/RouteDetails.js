import {List, Item, Text, Div} from "../dsl.js"
import styles from "../../styles.js";
import {LinkIcon, OuterLinkIcon} from "../LinkIcon.js"

/**
 * RouteDetails component
 *
 * @param {*} route route to display
 * @returns An "ul" element containing a list of "li" route properties.
 */
export default function RouteDetails(route) {

    return List('route',
            Div('route-display',
                Item('distance-item',
                    Text(styles.DETAIL_HEADER, 'Distance: '),
                    Text(styles.TEXT, `${route.distance} kilometers`)
                ),
                Div('locations',
                    Item('start-location-item',
                        Text(styles.TEXT, `${route.startLocation}`)
                    ),
                    Div('locations-line'),
                    Item('end-location-item',
                        Text(styles.TEXT, `${route.endLocation}`)
                    )
                )
            ),
            Div('route-user',
                LinkIcon(styles.USER_ICON, `#users/${route.user}`),
                OuterLinkIcon(styles.MAP_ICON, `https://www.google.com/maps/dir/${route.startLocation}/${route.endLocation}/`)
            )
        
        )
}
