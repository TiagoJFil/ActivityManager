import {List, Item, Text, Button} from '../utils.js'
import styles from "../../styles.js";

/**
 * ActivityList component
 *
 * @param {*} activities activities to display
 * @returns An "ul" element containing a list of "li" elements for each Activity
 */
export default function ActivityList(activities) {
    return List(styles.LIST,
            ...activities.map(activity =>
                Item(styles.LIST_ELEMENT,
                    Button(styles.LIST_BUTTON,
                        () => {
                            location.href = `#sports/${activity.sport}/activities/${activity.id}`
                        },
                        Text(styles.TEXT,
                            `${activity.id} `
                        )
                    )
                )
            )
    )
}

