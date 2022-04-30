import {userApi} from '../api.js'
import UserDetails from '../components/details/UserDetails.js'
import UserList from '../components/lists/UserList.js'
import {Pagination, getPaginationQuery} from '../components/Pagination.js'
import {queryBuilder, onPaginationChange} from './app-handlers.js'
import { H1 , Div } from '../components/dsl.js'
import styles from '../styles.js'

/**
 * Displays an user list with the given query
 */
async function displayUserList(mainContent, _, query) {
    const users = await userApi.fetchUsers(queryBuilder(query) || getPaginationQuery())
    const userCount = await userApi.fetchUsersCount()

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Users'),
        UserList(users),
        Pagination(userCount, (skip, limit) => onPaginationChange("users", query, skip, limit))
    )
}
/**
 * Displays the details of the user with the id from the params.
 */
async function displayUserDetails(mainContent, params, _) {
    const user = await userApi.fetchUser(params.uid)

    mainContent.replaceChildren(
        UserDetails(user)
    )
}

/**
 * Displays a list of users that have the given SportID and RouteID
 */
async function displayUsersByActivity(mainContent, params, query) {
    const users = await userApi.fetchUsersByActivity(queryBuilder(query) || getPaginationQuery(), params.sid)
    const userCount = await userApi.fetchUserByActivityCount(queryBuilder(query), params.sid)
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Users'),
        UserList(users),
        Pagination(userCount, (skip, limit) => onPaginationChange(`sports/${activities.sports}/users`, query, skip, limit))
    )
}

export const userHandlers = {
    displayUserDetails,
    displayUserList,
    displayUsersByActivity
}