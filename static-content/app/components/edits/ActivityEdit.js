import {List, Item, Text, Div, Input} from "../dsl.js"
import {ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import RouteSearch from "../searches/RouteSearch.js";
import Modal from "../Modal.js";
import {ErrorToast, InfoToast} from '../../toasts.js'
import {DatePicker} from "../filters/ActivitySearchFilter.js"

const DURATION_REGEX = /^(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]$/

/**
 * Activity editing component
 * Makes a modal window for editing an activity
 * 
 * @param {Object} activity - the activity to edit
 * @param {Function} onEditConfirm - callback to call when the activity is edited
 * @param {Function} onRouteChange - callback to call when the route is changed
 * @param {Object} route - the route associated with the activity
 */
export default function ActivityEdit(activity, onEditConfirm, onRouteChange, route){

    const durationInput = Input('editedDuration','text', null, null, "e.g 10:10:10.555", activity.duration, true)
    const date = DatePicker(activity.date)
    const sLocation = route ? route.startLocation : ""
    const eLocation = route ? route.endLocation : ""
    const routeSearch =  RouteSearch(onRouteChange,"Select a route", sLocation, eLocation)


    const onClickConfirm = async () => {
        const editedDuration= durationInput.value
        const editedDate = date.value
        const editedRouteId = document.querySelector('#routeSelector').value

        let toasts = [];

        if(editedDate.length <= 0){
            toasts.push(ErrorToast("Date cannot be empty."))
        }
        if(editedDuration.length <= 0) {
            toasts.push(ErrorToast("Duration cannot be empty."))
        }
        
        if(!DURATION_REGEX.test(editedDuration)){
            toasts.push(ErrorToast("Duration must be in the format hh:mm:ss.fff"))
        }

        if(toasts.length > 0){
            toasts.forEach(toast => toast.showToast())
            return
        }  


        const success = await onEditConfirm(editedDate,editedDuration,editedRouteId)
        if (!success) return

        modal.style.display = "none";
        const dateOnDisplay = document.querySelector('.date-item .text')
        const durationOnDisplay = document.querySelector('.duration-item .text')
        

        dateOnDisplay.textContent = editedDate
        
        durationOnDisplay.textContent = editedDuration
    }
   
    const resetEdit = async () => {
        const dateOnDisplay = document.querySelector('.date-item .text')
        const durationOnDisplay = document.querySelector('.duration-item .text')
        
        const sLocationOnDisplay = document.querySelector('#startLocationDL')
        const eLocationOnDisplay = document.querySelector('#endLocationDL')
        
    
        date.value = dateOnDisplay.textContent 
        durationInput.value = durationOnDisplay.textContent
        sLocationOnDisplay.value = sLocation
        eLocationOnDisplay.value = eLocation

        InfoToast("Undo sucessfull").showToast()
    }

    const modal = Modal('activity-edit',"Edit the activity",
        List(styles.DETAILS,
            date,
            Item('duration-item',
                Text(styles.DETAIL_HEADER, 'Duration: '),
                durationInput,
            ),
           routeSearch
        ),
        Div(styles.ICON_GROUP,
            ButtonIcon(styles.UNDO_ICON, resetEdit, 'Undo'),
            ButtonIcon(styles.CHECK_ICON, onClickConfirm, 'Confirm')
        )
    )
    return modal

}