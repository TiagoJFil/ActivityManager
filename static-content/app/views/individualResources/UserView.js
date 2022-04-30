import {List, Item, Text, Div, H1} from "../utils.js"
import ActivityLinkIcon from "../ActivityLinkIcon.js";
import styles from "../../styles.js";

export default function UserView(user){
    return Div(styles.HEADER_DIV,
        H1(styles.HEADER, 'User Details'),
        List(styles.DETAILS,
            Item('name-item',
                Text(styles.DETAIL_HEADER, 'Name: '),
                Text(styles.TEXT, user.name)
            ),
            Item('email-item',
                Text(styles.DETAIL_HEADER, 'Email: '),
                Text(styles.TEXT,user.email)
            ),
            ActivityLinkIcon(`#users/${user.id}/activities`)
        )
    )
}

