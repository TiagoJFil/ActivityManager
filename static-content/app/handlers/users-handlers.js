import {userApi, sportApi, routeApi} from '../api/api.js'
import UserDetails from '../components/details/UserDetails.js'
import UserList from '../components/lists/UserList.js'
import {getItemsPerPage, Pagination} from '../components/Pagination.js'
import { onPaginationChange} from './app-handlers.js'
import { H1, Div, Option} from '../components/dsl.js'
import UserRankingFilter from '../components/filters/UserRankingFilter.js'
import styles from '../styles.js'
import {queryBuilder} from "../api/api-utils.js";


/**
 * Displays the page to search for the users.
 */
async function displayUsersByRanking(mainContent, _, __) {
    // Replaces Sport's selector with sports that are relevant to the typed text
    const onSportTextChange = async (sportText) =>{
        const sportSelector = document.querySelector("#sportSelector")
        /*
        if(sportText.length < 3) {
            sportSelector.replaceChildren()
            return
        }*/

        const sportList = await sportApi.fetchSports({search : sportText})
        const sportSelectorOptions = sportList.sports.map(sport => Option(styles.SELECTOR_OPTION, sport.id, sport.name))

        sportSelector.replaceChildren(
            ...sportSelectorOptions
        )
    }

    // Replaces Route's selector with routes that are relevant to both of the locations
    const onLocationsChange = async (startLocation, endLocation) => {
        const routeSelector = document.querySelector("#routeSelector")

        /*
        if(startLocation.length < 3 && endLocation.length < 3) {
            routeSelector.replaceChildren()
            return
        }*/

        const newRouteQuery = {
            startLocation,
            endLocation
        }

        const routeList = await routeApi.fetchRoutes(newRouteQuery)

        const routeSelectorOptions = routeList.routes.map( (route) =>
            Option(styles.SELECTOR_OPTION, `${route.id}`,
                `${route.startLocation} - ${route.endLocation}`
             )
        )
        
        routeSelector.replaceChildren(...routeSelectorOptions)
    } 

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
  
    mainContent.replaceChildren(
        H1(styles.HEADER, 'User ranking'),
        UserRankingFilter(onSportTextChange, onLocationsChange, onSubmit),
        Div(styles.SPACER)
    )   
}

/**
 * Displays an user list with the given query
 */
async function displayUserList(mainContent, _, query) {
    const userList = await userApi.fetchUsers(queryBuilder(query))

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Users'),
        UserList(userList.users),
        Pagination(userList.total, (skip, limit) => onPaginationChange("users", query, skip, limit))
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
    const userList = await userApi.fetchUsersByActivity(params.sid, query)
    mainContent.replaceChildren(
        H1(styles.HEADER, 'Users'),
        UserList(userList.users),
        Pagination(
            userList.total,
            (skip, limit) => onPaginationChange(`sports/${params.sid}/users`, query, skip, limit)
        )
    )
}

export const userHandlers = {
    displayUserDetails,
    displayUserList,
    displayUsersByActivity,
    displayUsersByRanking
}