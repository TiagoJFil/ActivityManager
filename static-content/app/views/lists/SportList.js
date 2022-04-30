import {List, Item, Text, Button} from '../utils.js'
import styles from "../../styles.js";

/**
 * SportList component
 *
 * @param {*} sports sports to display
 * @returns An "ul" element containing a list of "li" elements for each sport
 */
export default function SportList(sports) {
    return List(styles.LIST,
        ...sports.map(sport =>
            Item(styles.LIST_ELEMENT,
                Button(styles.LIST_BUTTON,
                    () => {
                        location.href = `#sports/${sport.id}`
                    },
                    Text(styles.TEXT,
                        sport.name
                    )
                )
            )
        )
    )
    
}