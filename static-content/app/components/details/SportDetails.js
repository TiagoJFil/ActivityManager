import {List, Item, Text, Button, Icon, Div, H1} from "../dsl.js"
import  {LinkIcon} from "../LinkIcon.js"
import styles from "../../styles.js";

/**
 * SportDetails component
 *
 * @param {Sport} sport the sport to display
 *
 * @returns {Div} the sport details component
 */
export default function SportDetails(sport) {
    const description = sport.description ?
        Div('sport-description',
            Text(styles.DETAIL_HEADER, 'Description: '),
            Text(styles.TEXT, sport.description)
        )
        :
        Text(styles.TEXT, "No description available")

    return Div(styles.HEADER_DIV,
        H1(styles.HEADER, 'Sport Details'),
        List(styles.DETAILS,
            Item('name-item',
                Text(styles.DETAIL_HEADER, 'Name: '),
                Text(styles.TEXT,sport.name)
            ),
            Item('description-item',
                description
            ),
            LinkIcon(styles.CALENDAR_ICON,`#sports/${sport.id}/activities`),
            LinkIcon(styles.USER_ICON, `#users/${sport.user}`)
        )
    )
}
