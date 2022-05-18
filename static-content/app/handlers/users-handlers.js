import {userApi, sportApi} from '../api.js'
import UserDetails from '../components/details/UserDetails.js'
import UserList from '../components/lists/UserList.js'
import {Pagination, getPaginationQuery} from '../components/Pagination.js'
import {queryBuilder, onPaginationChange} from './app-handlers.js'
import { H1, Div, Option} from '../components/dsl.js'
import UserRankingFilter from '../components/filters/UserRankingFilter.js'
import styles from '../styles.js'


/**
 * Displays the page to search for the users.
 */
async function displayUsersByRanking(mainContent, _, query) {

    const onSportTextChange = async (sportText) =>{
        const sportSelector = document.querySelector("#sportSelector")
        if(sportText.length < 3) {
            sportSelector.replaceChildren()
            return
        }
        const sports = await sportApi.fetchSports(`search=${sportText}`)
        const sportOptions = sports.map(sport => Option(styles.SELECTOR_OPTION, sport.id, sport.name))

        sportSelector.replaceChildren(
            ...sportOptions
        )
    } 

    const onSubmit = (rid,sid) =>{
        window.location.hash = `sports/${sid}/user?rid=${rid}`
    }
  
    mainContent.replaceChildren(
        Div("rankings-main", 
            H1(styles.HEADER, 'User ranking'),
            UserRankingFilter(onSportTextChange, onSubmit)
        )
    )   
}

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
        Pagination(userCount, (skip, limit) => onPaginationChange(`sports/${params.sid}/users`, query, skip, limit))
    )
}

export const userHandlers = {
    displayUserDetails,
    displayUserList,
    displayUsersByActivity,
    displayUsersByRanking
}