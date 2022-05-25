import {List, Item, Text, Div} from "../dsl.js"
import styles from "../../styles.js";
import {LinkIcon, OuterLinkIcon, ButtonIcon} from "../Icons.js"
import RouteEdit  from '../edits/RouteEdit.js'

/**
 * RouteDetails component
 * Contains the details of a route and its links in form of buttons
 *
 * @param {Route} route route to display
 * @returns {List} the route details component
 */
export default function RouteDetails(route, onEditConfirm) {

    const modal = RouteEdit(route,onEditConfirm)
    
    const onEdit = () => {
        modal.style.display = "flex";
    }

    return List('route',
            Div('route-display',
                Item('distance-item',
                    Text(styles.DETAIL_HEADER, 'Distance: '),
                    Text(styles.TEXT, `${route.distance}`),
                    Text(styles.TEXT, ' Kilometers')
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
            Div(styles.ICON_GROUP,
                LinkIcon(styles.USER_ICON, `#users/${route.user}`, "Get users details"),
                OuterLinkIcon(styles.MAP_ICON, `https://www.google.com/maps/dir/${route.startLocation}/${route.endLocation}/`),
                ButtonIcon(styles.EDIT_ICON, onEdit, "Edit route")           
            ),
            modal
        
        )
}
