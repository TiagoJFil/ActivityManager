import { List, Item, Text ,Anchor} from '../utils.js'


/**
 * ActivityList component
 *
 * @param {*} activities activities to display
 * @returns An "ul" element containing a list of "li" elements for each Activity
 */
export default function ActivityList(activities){
    return List('list',
        ...activities.map(activity =>
            Item('activity',
                Anchor('link', `#activities/${activity.id}`,
                        Text("text",
                            `${activity.name}`
                        )
                )
            )
        )
    )
}