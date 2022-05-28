import { routeApi } from '../api/api.js'
import RouteList from '../components/lists/RouteList.js'
import RouteDetails from '../components/details/RouteDetails.js'
import {getItemsPerPage, Pagination} from '../components/Pagination.js'
import { onPaginationChange} from './app-handlers.js'
import styles from '../styles.js'
import { H1, Div} from '../components/dsl.js'
import SearchBar from '../components/SearchBar.js'
import RouteCreate from "../components/creates/CreateRoute.js";
import { SuccessToast, ErrorToast ,InfoToast} from '../toasts.js'
import { BoardlessIconButton } from "../components/Icons.js";

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

    const newQuery = {
        limit: query.limit ?? getItemsPerPage(),
        skip: 0
    }

    const onStartLocationTextChange = async (searchText) => {
        newQuery.startLocation = searchText

        await updateRouteDisplayItems(newQuery, listElement)
    }

    const onEndLocationTextChange = async (searchText) => {
        newQuery.endLocation = searchText

       await updateRouteDisplayItems(newQuery)
    }



    mainContent.replaceChildren(
        H1(styles.HEADER, 'Routes'),
        Div(styles.SEARCH_BAR_WITH_ADD,
            SearchBar("searchRes", styles.FORM_TEXT_INPUT, onStartLocationTextChange, "Search for a starting location", "Start Location"),
            SearchBar("searchRes", styles.FORM_TEXT_INPUT, onEndLocationTextChange, "Search for an ending location", "End Location"),
            BoardlessIconButton(`#routes/add`,"Add a route")
        ),
        listElement,
        paginationElement
    )


    async function updateRouteDisplayItems(newQuery) {
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
}


/**
 * Displays a route details with the given id
 */
async function displayRouteDetails(mainContent, params, _) {

    const route = await routeApi.fetchRoute(params.rid)

    const onEditConfirm = async (sLocation, eLocation, distance) => {
        return routeApi.updateRoute(route.id, sLocation, eLocation, distance)
        .then( () => {
            SuccessToast("Saved!").showToast()
            return true
        }).catch( (e) => {
            ErrorToast("Error updating route").showToast()
            InfoToast(e.message).showToast()
            return false
        })
    }

    mainContent.replaceChildren(
        H1(styles.HEADER, 'Route Details'),
        RouteDetails(route,onEditConfirm),
        Div(styles.SPACER)
    )
}


/**
 * Displays the route creation page
 */
async function createRoute(mainContent, params, _) {
    const nItems = getItemsPerPage()

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

        window.location.hash = `routes?skip=0&limit=${nItems}`
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