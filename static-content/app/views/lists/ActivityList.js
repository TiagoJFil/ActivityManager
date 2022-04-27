import { List, Item, Text ,Anchor, Div, H1} from '../utils.js'


/**
 * ActivityList component
 *
 * @param {*} activities activities to display
 * @returns An "ul" element containing a list of "li" elements for each Activity
 */
export default function ActivityList(activities){
    return Div('header-div', H1('header', 'Activities for:'),
        List('list',
            ...activities.map(activity =>
                Item('activity',
                    Anchor('link', `#activities/${activity.id}`,
                        Text("text",
                            `${activity.id}`
                        )
                    )
                )
            )
        )
    )
}