import {List, Item, Text, UserLink, Anchor, Button} from "./utils.js"


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
        Item('user-link-item',
            UserLink('Created by: ','this user',activity.user)
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
            `#sports/${activity.sport}/users?rid=${activity.route}`,
            'blalbbla'
        ),
        //TODO VERFICAR SE A ROUTE ANTES DE POR ISTO

    )
}
