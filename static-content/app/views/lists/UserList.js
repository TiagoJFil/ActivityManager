import { List, Item , Anchor , Text} from '../utils.js'

/**
 * UserList component
 * 
 * @param {*} users users to display
 * @returns An "ul" element containing a list of "li" elements for each user
 */
export default function UserList(users){
    return List('list',
        ...users.map(user =>
            Item('user',
                Anchor('link', `#users/${user.id}`,
                    Text("text",
                        user.name
                    )
                )
            )
        )
    )
}