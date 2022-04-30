import {Button, Icon} from "./utils.js";
import styles from "../styles.js";

export default function ActivityLinkIcon(href) {

    return Button(styles.ICON_BUTTON_CLASS,
        () => {
            location.href = href
        },
        Icon(styles.BX_CLASS, 'bx-calendar-event')
    )

}