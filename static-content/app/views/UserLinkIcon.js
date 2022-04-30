import {Button, Icon} from "./utils.js";
import styles from "../styles.js";

export default function UserLinkIcon(userId){

    return Button(styles.ICON_BUTTON_CLASS,
        () => {
            location.href = `#users/${userId}`
        },
        Icon(styles.BX_CLASS, 'bx-user')
    )

}