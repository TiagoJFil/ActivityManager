import {List, Item, Text, UserLink, Anchor} from "./utils.js"

//
// export default function ActivityView(activity){
//     const routeElem = activity.route ? Item('route-item',
//         Text('route-text', `Route: `),
//         Anchor('link',`#routes/${activity.route}`,
//             Text('text',
//                 'this route'
//             )
//         )
//     ) : Item('route-item',
//         Text('route-text', `Route: `),
//         Text('text',
//             'none'
//         )
//     )
//     return List('sport',
//         Item('date-item',
//             Text('date-text',
//                 `Date : ${activity.date}`
//             )
//         ),
//         Item('duration-item',
//             Text('duration-text',
//                 `Duration: ${activity.duration}`
//             )
//         ),
//         Item('user-link-item',
//             UserLink('Created by: ','this user',activity.user)
//         ),
//         Item('sport-link-item',
//             Text('sport-text', 'Sport:'),
//             Anchor('link',`#sports/${activity.sport}`,
//                 Text('text',
//                     'this sport'
//                 )
//             ),
//         ),
//         routeElem
//     )
// }
