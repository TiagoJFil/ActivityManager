import {List, Item, Text, Div, H1, Button} from '../utils.js'

/**
 * UserList component
 * 
 * @param {*} users users to display
 * @returns An "ul" element containing a list of "li" elements for each user
 */
export default function UserList(users){
    return Div('header-div',
        H1('header', 'Users'),
        List('list',
            ...users.map(user =>
                Item('resource-item',
                    Button('list-button',
                        () => {
                            location.href= `#users/${user.id}`
                        },
                        Text("text",
                            user.name
                        )
                    )
                )
            )
        )
    )
}