import {List, Item, Text, Anchor, Button} from "./utils.js"


export default function ActivityView(activity){
    const routeText = activity.route ?
            Anchor('link',`#routes/${activity.route}`,
                Text('text', 'this route')
            )
         :
            Text('text', 'none')

    return List('sport',
        Item('date-item',
            Text('date-text',
                `Date : ${activity.date}`
            )
        ),
        Item('duration-item',
            Text('duration-text',
                `Duration: ${activity.duration}`
            )
        ),
        Item('sport-link-item',
            Text('sport-text', 'Sport:'),
            Anchor('link',`#sports/${activity.sport}`,
                Text('text',
                    'this sport'
                )
            ),
        ),
        Item('route-item',
            Text('route-text', `Route: `),
            routeText
        ),
        Button('todo',
            () => {
                window.location.href =`#sports/${activity.sport}/users?rid=${activity.route}`
            },
            Text('text', 'Users that have the same sport and routes')
        ),
    )
}
