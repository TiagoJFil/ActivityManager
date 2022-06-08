import {userApi} from '../api/api.js'
import UserDetails from '../components/details/UserDetails.js'
import UserList from '../components/lists/UserList.js'
import { Pagination } from '../components/Pagination.js'
import { onPaginationChange} from './app-handlers.js'
import { H1, Div} from '../components/dsl.js'
import UserRankingFilter from '../components/filters/UserRankingFilter.js'
import styles from '../styles.js'
import {queryBuilder} from "../api/api-utils.js";
import { onRouteLocationsChange, onSportTextChange } from './utils.js'


/**
 * Displays the page to search for the users.
 */
async function displayUsersByRanking(_, __) {
    // Loads user rankings list
    const onSubmit = (rid,sid) =>{
        if(rid === "" || sid === "") {
            Toastify({
                text: "Please select a route and a sport",
                backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)",
                oldestFirst: false,
            }).showToast()
            return
        }
        window.location.hash = `sports/${sid}/user?rid=${rid}`
    }
  
    return[
        H1(styles.HEADER, 'User ranking'),
        UserRankingFilter(onSportTextChange, onRouteLocationsChange, onSubmit),
        Div(styles.SPACER)
    ]
}

/**
 * Displays an user list with the given query
 */
async function displayUserList(_, query) {
    const userList = await userApi.fetchUsers(queryBuilder(query))

    return[
        H1(styles.HEADER, 'Users'),
        UserList(userList.users),
        Pagination(userList.total, (skip, limit) => onPaginationChange("users", query, skip, limit))
    ]
}
/**
 * Displays the details of the user with the id from the params.
 */
async function displayUserDetails(params, _) {
    const user = await userApi.fetchUser(params.uid)

    return[
        UserDetails(user)
    ]
}

/**
 * Displays a list of users that have the given SportID and RouteID
 */
async function displayUsersByActivity(params, query) {
    const userList = await userApi.fetchUsersByActivity(params.sid, query)
    
    return[
        H1(styles.HEADER, 'Users'),
        UserList(userList.users),
        Pagination(
            userList.total,
            (skip, limit) => onPaginationChange(`sports/${params.sid}/users`, query, skip, limit)
        )
    ]
}

export const userHandlers = {
    displayUserDetails,
    displayUserList,
    displayUsersByActivity,
    displayUsersByRanking
}