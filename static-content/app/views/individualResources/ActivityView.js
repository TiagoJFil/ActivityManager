import {List, Item, Text, Button, Div, H1, Icon} from "../utils.js"
import UserLinkIcon from "../UserLinkIcon.js"
import styles from "../../styles.js";

export default function ActivityView(activity) {
     const emptyTest = Text(styles.TEXT, '')
     const routeButton = activity.route ? Button(styles.ICON_BUTTON_CLASS,
        () => {
            location.hash = `#routes/${activity.route}`
        },
        Icon(styles.BX_CLASS, 'bx-trip')
    ) : emptyTest

    const usersButton = activity.route ? Button(styles.ICON_BUTTON_CLASS,
        () => {
            location.hash = `#sports/${activity.sport}/users?rid=${activity.route}`
        },
        Icon(styles.BX_CLASS, 'bx-group')
    ) : emptyTest

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
            Button(styles.ICON_BUTTON_CLASS,
                () => {
                    location.hash = `#sports/${activity.sport}`
                },
                Icon(styles.BX_CLASS, 'bx-football')
            ),
            UserLinkIcon(activity.user),
            routeButton, usersButton
        )
    )
}
