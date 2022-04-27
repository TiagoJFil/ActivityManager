import {List, Item, Text, Div, H1, Button} from '../utils.js'


/**
 * ActivityList component
 *
 * @param {*} activities activities to display
 * @returns An "ul" element containing a list of "li" elements for each Activity
 */
export default function ActivityList(activities, headerText) {
    return Div('header-div',
        H1('header', headerText),
        List('list',
            ...activities.map(activity =>
                Item('resource-item',
                    Button('list-button',
                        () => {
                            location.href = `#sports/${activity.sport}/activities/${activity.id}`
                        },
                        Text("text",
                            `${activity.id} `
                        )
                    )
                )
            )
        )
    )
}