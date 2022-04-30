import {List, Item, Text, Div, H1} from "../dsl.js"
import {LinkIcon} from "../LinkIcon.js"
import styles from "../../styles.js";

/**
 * UserDetails component
 * Contains the details of a user and its links in form of buttons
 * 
 * @param {User} user 
 * @returns {Div} the user details component
 */
export default function UserDetails(user){
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
            LinkIcon(styles.CALENDAR_ICON,`#users/${user.id}/activities`)
        )
    )
}

