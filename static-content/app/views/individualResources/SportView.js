import {List, Item, Text, Button, Icon, Div, H1} from "../utils.js"
import ActivityLinkIcon from "../ActivityLinkIcon.js"
import UserLinkIcon from "../UserLinkIcon.js";
import styles from "../../styles.js";

export default function SportView(sport) {
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
            ActivityLinkIcon(`#sports/${sport.id}/activities`),
            UserLinkIcon(sport.user)
        )
    )
}
