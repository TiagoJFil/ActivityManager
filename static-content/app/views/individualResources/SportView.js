import {List, Item, Text, Button, Icon, Div, H1} from "../utils.js"
import ActivityLinkIcon from "../ActivityLinkIcon.js"
import UserLinkIcon from "../UserLinkIcon.js";


export default function SportView(sport) {
    const description = sport.description ?
        Div('sport-description',
            Text('description-header', 'Description: '),
            Text('description-text', sport.description)
        )
        :
        Text('description-text', "No description available")

    return Div('header-div',
        H1('header', 'Sport Details'),
        List('sport-details',
            Item('name-item',
                Text('detail-header', 'Name: '),
                Text('sport-name-text',sport.name)
            ),
            Item('description-item',
                description
            ),
            ActivityLinkIcon(`#sports/${sport.id}/activities`),
            UserLinkIcon(sport.user)
        )
    )
}
