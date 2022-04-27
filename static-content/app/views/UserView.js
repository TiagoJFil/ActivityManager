import {List, Item, Text, Button, Icon, Div, H1} from "./utils.js"

export default function UserView(user){
    return Div('header-div',
        H1('header', 'User Details'),
        List('user-details',
            Item('name-item',
                Text('name-header', 'Name: '),
                Text('name-text', user.name)
            ),
            Item('email-item',
                Text('email-header', 'Email: '),
                Text('email-text',user.email)
            ),
            Button('icon-button',
                () => {
                    location.href= `#users/${user.id}/activities`
                },
                Icon('bx','bx-calendar-event')
            )
        )
    )
}

