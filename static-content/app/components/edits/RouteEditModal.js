import {List, Item, Text, Div, Input} from "../dsl.js"
import  {ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import Modal from "../Modal.js";
import {ErrorToast, InfoToast} from '../../toasts.js'


const DECIMAL_PLACES_DISTANCE = 2
const LOCATION_MAX_LENGTH = 30


/**
 * Route editing component
 * Makes a modal window for editing a route
 *
 * @param {Object} route - the route to edit
 * @param {Function} onEditConfirm - callback to call when the confirm button is clicked
 */
export default function RouteEditModal(route, onEditConfirm) {

    const startLocationInput = Input('editedSLocation', 'text', null, null, "e.g Lisboa", route.startLocation, true)
    const endLocationInput = Input('editedELocation', 'text', null, null, "e.g Porto", route.endLocation, false)
    const distanceInput = Input('editedDistance', 'number', null, null, "e.g 320.5", route.distance, false, 0, 50000)

    const onClickConfirm = async () => {
        const editedSLocation = startLocationInput.value
        const editedELocation = endLocationInput.value
        const editedDistance = distanceInput.value

        let toasts = [];

        if (editedSLocation.length <= 0 || editedELocation.length <= 0) {
            toasts.push(ErrorToast("Location cannot be empty."))
        }
        if (editedSLocation.length > LOCATION_MAX_LENGTH || editedELocation > LOCATION_MAX_LENGTH) {
            toasts.push(ErrorToast("Location cannot have more than 150 characters."))
        }
        if(editedDistance === ""){
            toasts.push(ErrorToast("Distance can't be empty."))
        }
        if(isNaN(editedDistance)) { 
            toasts.push(ErrorToast("Distance must be a number."))
        }

        if(toasts.length > 0){
            toasts.forEach(toast => toast.showToast())
            return
        }
        
            
        const success = await onEditConfirm(editedSLocation, editedELocation, editedDistance)
        if (!success) return

        modal.style.display = "none";

        const sLocationOnDisplay = document.querySelector('.start-location-item .text')
        sLocationOnDisplay.textContent = editedSLocation
        
        const eLocationDisplay = document.querySelector('.end-location-item .text')
        eLocationDisplay.textContent = editedELocation

        const distanceDisplay = document.querySelector('.distance-item .text')
        distanceDisplay.textContent = Number(editedDistance).toFixed(DECIMAL_PLACES_DISTANCE)
    }
   
    const resetEdit = () => {
        const sLocationOnDisplay = document.querySelector('.start-location-item .text')
        const eLocationDisplay = document.querySelector('.end-location-item .text')
        const distanceDisplay = document.querySelector('.distance-item .text')

        startLocationInput.value = sLocationOnDisplay.textContent 
        endLocationInput.value = eLocationDisplay.textContent
        distanceInput.value = distanceDisplay.textContent

        InfoToast("Undo sucessfull").showToast()
    }

    const modal =  
        Modal('route-edit',"Edit the route",
            List(styles.DETAILS,
                Item('start-location-item',
                    Text(styles.DETAIL_HEADER, 'Start Location: '),
                    startLocationInput,
                ),
                Item('end-location-item',
                    Text(styles.DETAIL_HEADER, 'End Location: '),
                    endLocationInput,
                ),
                Item('distance-item',
                    Text(styles.DETAIL_HEADER, 'Distance: '),
                    distanceInput,
                )
            ),
            Div(styles.ICON_GROUP,
                ButtonIcon(styles.UNDO_ICON, resetEdit, 'Undo'),
                ButtonIcon(styles.CHECK_ICON, onClickConfirm, 'Confirm')
            )
        )

    return modal

}