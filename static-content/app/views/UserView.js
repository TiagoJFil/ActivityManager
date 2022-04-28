import {List, Item, Text, Div, H1} from "./utils.js"
import ActivityLinkIcon from "./ActivityLinkIcon.js";

export default function UserView(user){
    return Div('header-div',
        H1('header', 'User Details'),
        List('user-details',
            Item('name-item',
                Text('detail-header', 'Name: '),
                Text('name-text', user.name)
            ),
            Item('email-item',
                Text('detail-header', 'Email: '),
                Text('email-text',user.email)
            ),
            ActivityLinkIcon(`#users/${user.id}/activities`)
        )
    )
}

