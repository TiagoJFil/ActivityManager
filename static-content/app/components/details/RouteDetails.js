import {Div, HiddenElem, Item, List, Text} from "../dsl.js"
import styles from "../../styles.js";
import {ButtonIcon, LinkIcon, OuterLinkIcon} from "../Icons.js"
import RouteEditModal from '../edits/RouteEditModal.js'

/**
 * RouteDetails component
 * Contains the details of a route and its links in form of buttons
 *
 * @param {Route} route route to display
 * @param isOwner auto-explanatory
 * @param onEditConfirm function called when the confirm button is pressed
 * @returns {List} the route details component
 */
export default function RouteDetails(route, onEditConfirm, isOwner) {

    const modal = RouteEditModal(route, onEditConfirm)

    const onEdit = () => {
        modal.style.display = "flex";
    }

    const editButton = isOwner ? ButtonIcon(styles.EDIT_ICON, onEdit, "Edit route") : HiddenElem()


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
                editButton,       
            ),
            modal
        
        )
}
