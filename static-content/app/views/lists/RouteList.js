import {List, Item, Text, Button} from '../utils.js'
import styles from "../../styles.js";

/**
 * RouteList component
 *
 * @param {*} routes routes to display
 * @returns An "ul" element containing a list of "li" elements for each route
 */
export default function RoutesList(routes) {
    return List(styles.LIST,
            ...routes.map(route =>
                Item(styles.LIST_ELEMENT,
                    Button(styles.LIST_BUTTON,
                        () => {
                            location.href = `#routes/${route.id}`
                        },
                        Text(styles.TEXT,
                            `${route.startLocation} - ${route.endLocation}`
                        )
                    )
                )
            )
        )

}