import { List, Item, Text } from "./utils.js"

export default function UserView(user){
    return List('user',
        Item('name-item',
            Text('name-text',`name : ${user.name}`)
        ),
        Item('email-item',
            Text('email-text',`email : ${user.email}`)
        )
    )
}

