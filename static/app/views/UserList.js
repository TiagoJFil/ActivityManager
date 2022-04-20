import { List, Item } from './utils.js'

/**
 * UserList component
 * 
 * @param {*} users users to display
 * @returns An "ul" element containing a list of "li" elements for each user
 */
export default function UserList(users){
    return List('user-list',
        ...users.map(user =>
            Item('user',
                user.name.linkTo(`#users/${user.id}`)
            )
        )
    )
}