import { activityApi, routeApi, sportApi, userApi } from '../api/api.js'
import ActivityDetails from '../components/details/ActivityDetails.js'
import ActivityList from '../components/lists/ActivityList.js'
import {Pagination} from '../components/Pagination.js'
import {H1, Div} from '../components/dsl.js'
import {onPaginationChange} from './app-handlers.js'
import {queryBuilder} from "../api/api-utils.js";
import ActivitySearchFilter from '../components/filters/ActivitySearchFilter.js'
import ActivityCreate from '../components/creates/CreateActivity.js'
import styles from '../styles.js'
import { ErrorToast , InfoToast, SuccessToast } from '../toasts.js'
import { onRouteLocationsChange, DURATION_REGEX, onSportTextChange } from './utils.js'

/**
 * Displays an activity list with the given query
 */
 async function displayActivityList( params, query) {
    const activityList = await activityApi.fetchActivitiesBySport(params.sid, query)

    const activities = query.deleted
        ? activityList.activities.filter((activity) => activity.id !== query.deleted)
        : activityList.activities

    const total = query.deleted ? activityList.total - 1 : activityList.total
    const sport = params.sid ? await sportApi.fetchSport(params.sid) : null

    return [
        H1(styles.HEADER, `Activities for ${sport.name}`),

        ActivityList(activities),
        Pagination(
            total,
            (skip, limit) => onPaginationChange(`sports/${params.sid}/activities`, query, skip, limit)
        )
    ]
}


/**
 * Displays a list of activities for the given sport with the given query
 */
async function displaySearchActivities( _, query) {

    const onFilterSubmit = (date, sid, rid, sortOrder) => {

        if(!sid){
            InfoToast("Please select a sport").showToast()
            return
        }

        const newQuery = query 
        if(date) newQuery.date = date
        if(date === '') delete newQuery.date
        if(newQuery.rid !== rid){
            newQuery.rid = rid
        }
        if(!rid) delete newQuery.rid
        if(sortOrder) newQuery.orderBy = sortOrder
        
        window.location.hash = `#sports/${sid}/activities?${queryBuilder(newQuery)}`
    } 
    

    return [
        H1(styles.HEADER, `Activity search`),
        ActivitySearchFilter(onFilterSubmit, onRouteLocationsChange, onSportTextChange, query),
    ]
}

/**
 * Displays the activities from the given user with the given query
 */
async function displayActivitiesByUser( params, query) {
    const activityList = await activityApi.fetchActivitiesByUser(params.uid, query)
    const user = await userApi.fetchUser(params.uid)

    return [
        H1(styles.HEADER, `Activities for ${user.name}`),
        ActivityList(activityList.activities),
        Pagination(
            activityList.total,
            (skip, limit) => onPaginationChange(`users/${params.uid}/activities`, query, skip, limit)
        )
    ]
}

/**
 * Displays the activity details for the given activity id
 */
async function displayActivityDetails( params, _) {
    const activity = await activityApi.fetchActivity(params.sid, params.aid)
    const route = activity.route ? await routeApi.fetchRoute(activity.route) : null

    const onDeleteConfirm = async () => {
        activityApi.deleteActivity(params.sid, params.aid)
            .then(() => {
                SuccessToast("Activity deleted").showToast()
                return true
            }).catch((e) => {
                ErrorToast("Error deleting sport").showToast()
                InfoToast(e.message).showToast()
                window.location.href = `#sports/${params.sid}/activities`
                return false
            })
        
        window.location.hash = `#sports/${params.sid}/activities?deleted=${params.aid}`
    }

    const onEditConfirm = async (date, duration, rid) => {

        activityApi.updateActivity(params.sid, params.aid, date, duration, rid)
        .then(() => {
            SuccessToast("Saved!").showToast()
            const routeButton = document.querySelector("#route-link")
            routeButton.onclick = () => window.location.href = `#routes/${rid}`
            return true
        }).catch((e) => {
            ErrorToast("Error updating sport").showToast()
            InfoToast(e.message).showToast()
            return false
        })
    }

    return [
        ActivityDetails(activity, onDeleteConfirm, onEditConfirm, onRouteLocationsChange, route)
    ]
}



/**
 * Displays the activty creation page
 */
async function createActivity( params, _){
    
    const onSubmit = async (date, duration, route) => {
        try{
            const toasts = []
                
            if(date.length <= 0){
                toasts.push(Toastify({
                    text: "Date cannot be empty.",
                    backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)"
                }))
            }
            if(duration.length <= 0) {
                toasts.push(Toastify({
                    text: "Duration cannot be empty.",
                    backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)"
                }))
            }
            
            if(!DURATION_REGEX.test(duration)){
                toasts.push(ErrorToast("Duration must be in the format hh:mm:ss.fff"))
                
            }
            if(toasts.length > 0){
                toasts.forEach((toast, i) => 
                    setTimeout(() => {
                        toast.showToast()
                    }, 300 * i)
                )
                return
            }
                
            
            await activityApi.createActivity(params.sid, date, duration, route)
        }catch(e){
            console.log(e)
            let message = ""
            if(e.code === 2000)
                message = "Invalid date or duration."
            else if(e.code === 2001)
                message = "Date and duration are required."   
            else if(e.code === 2003)
                message = "Try logging in to create a route."

            Toastify({
                text: message,
                backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)",
                oldestFirst: false,
            }).showToast()
            return
        }

        window.location.hash = `sports/${params.sid}`
    }

    return [
        H1(styles.HEADER, 'Add an Activity' ),
        Div(styles.ADD_CONTAINER,
            ActivityCreate(onSubmit, onRouteLocationsChange)
        )
    ]
}

export const activityHandlers = {
    displaySearchActivities,
    displayActivitiesByUser,
    displayActivityDetails,
    displayActivityList,
    createActivity
}
