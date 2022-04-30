import {List, Item, Text, Div, H1} from "../utils.js"
import styles from "../../styles.js";
import UserLinkIcon from "../UserLinkIcon.js";

export default function RouteView(route) {

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
                UserLinkIcon(route.user)
            )
            
        )
}
