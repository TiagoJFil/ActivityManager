import {List, Item, Text, Div, H1, Button} from '../utils.js'
import styles from "../../styles.js";

/**
 * UserList component
 * 
 * @param {*} users users to display
 * @returns An "ul" element containing a list of "li" elements for each user
 */
export default function UserList(users){
    return List(styles.LIST,
            ...users.map(user =>
                Item(styles.LIST_ELEMENT,
                    Button(styles.LIST_BUTTON,
                        () => {
                            location.href= `#users/${user.id}`
                        },
                        Text(styles.TEXT,
                            user.name
                        )
                    )
                )
            )
    )
}