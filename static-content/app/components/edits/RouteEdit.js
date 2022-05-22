import {List, Item, Text, Button, Icon, Div, H1,Input} from "../dsl.js"
import  {LinkIcon, ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import Modal from "../Modal.js";
import {ErrorToast, InfoToast} from '../../toasts.js'


export default function RouteEdit(route, onEditConfirm){

    const startLocationInput = Input('editedName','text', null, null, "e.g Football", route.startLocation, true)
    const endLocationInput = Input('editedDescription','text', null, null, "e.g Played with a ball", route.endLocation,false)
    const distanceInput = Input('editedDescription','number', null, null, "e.g Played with a ball", route.distance,false,0,50000)

    const onClickConfirm = async () => {
        const editedSLocation= startLocationInput.value
        const editedELocation = endLocationInput.value
        const editedDistance = distanceInput.value

        let toasts = [];

        if(editedSLocation.length <= 0 || editedELocation.length <= 0){
            toasts.push(ErrorToast("Location cannot be empty."))
        }
        if(editedSLocation.length > 30 || editedELocation > 30){
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
        distanceDisplay.textContent = Number(editedDistance).toFixed(3)
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
        Modal("Edit the route",
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