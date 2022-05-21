import { routeApi } from '../api/api.js'
import RouteList from '../components/lists/RouteList.js'
import RouteDetails from '../components/details/RouteDetails.js'
import {Pagination} from '../components/Pagination.js'
import { onPaginationChange} from './app-handlers.js'
import styles from '../styles.js'
import { H1, Div, Anchor, Icon} from '../components/dsl.js'
import SearchBar from '../components/SearchBar.js'
import RouteCreate from "../components/creates/CreateRoute.js";


/**
 * Displays a route list with the given query
 */
async function displayRouteList(mainContent, _, query) {

    const routeList = await routeApi.fetchRoutes(query)
    let listElement = RouteList(routeList.routes)

    const onPageChange = (skip, limit) => onPaginationChange("routes", query, skip, limit)

    let paginationElement = Pagination(
        routeList.total,
        onPageChange,
        query.limit
    )

    const onRouteTextChange = async (searchText) => {

        const newQuery = {
            search: searchText,
            limit: query.limit ?? getItemsPerPage(),
            skip: 0
        }

        const innerRoutesList = await routeApi.fetchRoutes(newQuery)
        const newPagination = Pagination(
            innerRoutesList.total,
            onPageChange,
            query.limit
        )
        const newListElement = RouteList(innerRoutesList.routes)

        listElement.replaceWith(
            newListElement
        )
        paginationElement.replaceWith(
            newPagination
        )

        listElement = newListElement
        paginationElement = newPagination
    }

    const addAnchor = Anchor("big",`#routes/add`, Icon(styles.BX_CLASS, styles.ADD_ICON))
    addAnchor.title = "Add a route"

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Routes'),
        Div(styles.SEARCH_BAR_WITH_ADD,
            SearchBar("searchRes", styles.FORM_TEXT_INPUT, onRouteTextChange, "Search for a route", null),
            addAnchor
        ),
        listElement,
        paginationElement
    )
}

/**
 * Displays a route details with the given id
 */
async function displayRouteDetails(mainContent, params, _) {

    const route = await routeApi.fetchRoute(params.rid)

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Route Details'),
        RouteDetails(route),
        Div(styles.SPACER)
    )
}

async function createRoute(mainContent, params, _) {
    const onSubmit = async (sLocation, eLocation, distance) => {
        try{
            const toasts = []
            if(sLocation === "") 
                toasts.push(Toastify({
                    text: "Please enter a start location",
                    backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)"
                }))
            if(eLocation === "")
                toasts.push(Toastify({
                    text: "Please enter an end location",
                    backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)"
                }))
            if(distance === "" || isNaN(distance))
                toasts.push(Toastify({
                    text: "Please provide a distance in kilometers",
                    backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)"
                }))

            if(toasts.length > 0){
                toasts.forEach((toast, i) => 
                    setTimeout(() => {
                        toast.showToast()
                    }, 300 * i)
                )
                return
            }
                
            
            await routeApi.createRoute(sLocation, eLocation, distance)
        }catch(e){
            console.log(e)
            let message = ""
            if(e.code === 2000)
                message = "Invalid start location, end location or distance."
            else if(e.code === 2001)
                message = "All fields are required."   
            else if(e.code === 2003)
                message = "Try logging in to create a route."

            Toastify({
                text: message,
                backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)",
                oldestFirst: false,
            }).showToast()
            return
        }

        window.location.hash = 'routes'
    }

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Add a Route'),
        Div(styles.ADD_CONTAINER,
            RouteCreate(onSubmit)
        )
    )
}


export const routeHandlers = {
    displayRouteDetails,
    displayRouteList,
    createRoute
}