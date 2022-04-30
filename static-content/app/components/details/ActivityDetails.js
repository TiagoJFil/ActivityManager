import {List, Item, Text, Button, Div, H1, Icon} from "../dsl.js"
import  {LinkIcon} from "../LinkIcon.js"
import styles from "../../styles.js";

export default function ActivityDetails(activity) {
    const emptyText = Text(styles.TEXT, '')

    return Div(styles.HEADER_DIV,
        H1(styles.HEADER, 'Activity Details'),
        List(styles.DETAILS,
            Item('date-item',
                Text(styles.DETAIL_HEADER, 'Date: '),
                Text(styles.TEXT, activity.date)
            ),
            Item('duration-item',
                Text(styles.DETAIL_HEADER, 'Duration: '),
                Text(styles.TEXT, activity.duration)
            ),
            LinkIcon(styles.SPORT_ICON,`#sports/${activity.sport}`),
            LinkIcon(styles.USER_ICON, `#users/${activity.user}`),
            activity.route 
            ? LinkIcon(styles.ROUTE_ICON,`#routes/${activity.route}`) : emptyText, // Route Link if route exists
            activity.route 
            ? LinkIcon(styles.USERS_ICON, `#sports/${activity.sport}/users?rid=${activity.route}`) : emptyText  // Users Link if route exists
        )
    )
}
