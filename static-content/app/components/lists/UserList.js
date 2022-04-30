import ResourceList from './ResourceList.js';

/**
 * UserList component
 * 
 * @param {*} users users to display
 * @returns An "ul" element containing a list of "li" elements for each user
 */
export default function UserList(users){
    return ResourceList( 
        users,
        (user) => `#users/${user.id}`,
        (user) => user.name
    )

}