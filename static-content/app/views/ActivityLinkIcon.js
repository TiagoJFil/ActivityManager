import {Button, Icon} from "./utils.js";


export default function ActivityLinkIcon(href) {

    return Button('icon-button',
        () => {
            location.href = href
        },
        Icon('bx', 'bx-calendar-event')
    )

}