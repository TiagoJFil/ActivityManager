import {List, Item, Text, Button, Div, H1, Icon} from "./utils.js"
import UserLinkIcon from "./UserLinkIcon.js"


export default function ActivityView(activity) {
     const emptyTest = Text('text', '')
     const routeButton = activity.route ? Button('icon-button',
        () => {
            location.href = `#users/${activity.user}`
        },
        Icon('bx', 'bx-trip')
    ) : emptyTest

    const usersButton = activity.route ? Button('icon-button',
        () => {
            location.href = `#sports/${activity.sport}/users?rid=${activity.route}`
        },
        Icon('bx', 'bx-group')
    ) : emptyTest

    return Div('header-div',
        H1('header', 'Activity Details'),
        List('activity-details',
            Item('date-item',
                Text('detail-header', 'Date: '),
                Text('date-text', activity.date)
            ),
            Item('duration-item',
                Text('detail-header', 'Duration: '),
                Text('duration-text', activity.duration)
            ),
            Button('icon-button',
                () => {
                    location.href = `#sports/${activity.sport}`
                },
                Icon('bx', 'bx-football')
            ),
            UserLinkIcon(activity.user),
            routeButton, usersButton
        )
    )
}
