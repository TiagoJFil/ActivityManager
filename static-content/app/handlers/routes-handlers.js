import {routeApi} from '../api/api.js'
import RouteList from '../components/lists/RouteList.js'
import RouteDetails from '../components/details/RouteDetails.js'
import {getItemsPerPage, Pagination} from '../components/Pagination.js'
import {onPaginationChange} from './app-handlers.js'
import styles from '../styles.js'
import {Div, H1, HiddenElem} from '../components/dsl.js'
import SearchBar from '../components/SearchBar.js'
import RouteCreate from "../components/creates/CreateRoute.js";
import {ErrorToast, InfoToast, SuccessToast} from '../toasts.js'
import {BoardlessIconButton} from "../components/Icons.js";
import {isLoggedIn} from "../api/session.js"
import {isOwner} from "./utils.js";

/**
 * Displays a route list with the given query
 */
async function displayRouteList(_, query) {

    const routeList = await routeApi.fetchRoutes(query)
    let listElement = RouteList(routeList.routes)

    const onPageChange = (skip, limit) => onPaginationChange("routes", query, skip, limit)

    let paginationElement = Pagination(
        routeList.total,
        onPageChange,
        query.limit
    )

    let newQuery = {
        limit: query.limit ?? getItemsPerPage(),
        skip: 0,
    }

    const onLocationTextChange = async (locationName, searchText) => {
        const other = locationName === "startLocation" ? "endLocation" : "startLocation"
        if (newQuery && query[locationName]) {
            newQuery = {
                limit: query.limit ?? getItemsPerPage(),
                skip: 0,
                [locationName]: searchText,
                [other]: query[other]
            }
        } else {
            newQuery[locationName] = searchText
        }

        await updateRouteDisplayItems(newQuery)
    }

    const addButton = isLoggedIn() ? BoardlessIconButton(`#routes/add`, "Add a route") : HiddenElem()
    const startingSLocationSearchBarValue = query.startLocation ?? null
    const startingELocationSearchBarValue = query.endLocation ?? null

    return[
        H1(styles.HEADER, 'Routes'),
        Div(styles.SEARCH_BAR_WITH_ADD,
            SearchBar(
                "startLocationSearch",
                styles.FORM_TEXT_INPUT,
                (searchText) => onLocationTextChange("startLocation", searchText),
                "Search for a starting location",
                "Start Location",
                startingSLocationSearchBarValue
            ),
            SearchBar(
                "endLocationSearch",
                styles.FORM_TEXT_INPUT,
                (searchText) => onLocationTextChange("endLocation", searchText),
                "Search for an ending location",
                "End Location",
                startingELocationSearchBarValue
            ),
            addButton
        ),
        listElement,
        paginationElement
    ]


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
async function displayRouteDetails(params, _) {

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

    return [
        H1(styles.HEADER, 'Route Details'),
        RouteDetails(route, onEditConfirm, isOwner(route.user)),
        Div(styles.SPACER)
    ]
}




/**
 * Displays the route creation page
 */
async function createRoute(params, _) {

    const onSubmit = async (sLocation, eLocation, distance) => {
        try {
            const toasts = []
            if (sLocation === "")
                toasts.push(ErrorToast("Please enter a starting location"))
            if (eLocation === "")
                toasts.push(ErrorToast("Please enter an end location"))
            if (distance === "" || isNaN(distance))
                toasts.push(ErrorToast("Please provide a distance in kilometers"))

            if (toasts.length > 0) {
                showToasts(toasts)
                return
            }

            await routeApi.createRoute(sLocation, eLocation, distance)
        }catch(e){
            let message = ""
            if(e.code === 2000)
                message = "Invalid start location, end location or distance."
            else if(e.code === 2001)
                message = "All fields are required."   
            else if(e.code === 2003)
                message = "Try logging in to create a route."

            ErrorToast(message).showToast()
            return
        }

        window.location.hash = `routes?skip=0&limit=${getItemsPerPage()}`
    }

    return[
        H1(styles.HEADER, 'Add a Route'),
        Div(styles.ADD_CONTAINER,
            RouteCreate(onSubmit)
        )
    ]
}


export const routeHandlers = {
    displayRouteDetails,
    displayRouteList,
    createRoute
}