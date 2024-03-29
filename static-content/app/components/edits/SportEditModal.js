import {List, Item, Text, Div, Input, TextArea} from "../dsl.js"
import  {ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import Modal from "../Modal.js";
import {ErrorToast, InfoToast} from '../../toasts.js'


const DESCRIPTION_TEXT = "No description available"


/**
 * Sport editing component
 * Makes a modal window for editing a sport
 * 
 * @param {Object} sport - the sport to edit
 * @param {Function} onEditConfirm - callback to call when the confirm button is clicked
 */
function SportEditModal(sport,onEditConfirm) {

    const nameInput = Input('edited-name','text', null, null, "e.g Football", sport.name, true)
    const descriptionInput = TextArea('edited-description','text', null, "e.g Game played with feet", sport.description, false)

    const onClickConfirm = async () => {
        const editedName = nameInput.value
        const editedDescription = descriptionInput.value

        if(editedName.length <= 0){
            ErrorToast("Name cannot be empty.").showToast()
            return
        }
        else if(editedName.length > 30){
            ErrorToast("Name cannot have more than 30 characters.").showToast()
            return
        }
            
        const success = await onEditConfirm(editedName, editedDescription)
        if (!success) return

        modal.style.display = "none";

        const nameOnDisplay = document.querySelector('.name-item .text')
        nameOnDisplay.textContent = editedName
        
        if(descriptionInput.value !== ""){
            const descriptionElem = document.querySelector('.description-item')
            const descriptionOnDisplay = document.querySelector('.description-item .text')
            if(descriptionOnDisplay.textContent === DESCRIPTION_TEXT){
                descriptionElem.prepend( Text(styles.DETAIL_HEADER, 'Description: '))
            }
            descriptionOnDisplay.textContent = editedDescription
        }else{
            const descriptionOnDisplay = document.querySelector('.description-item')
            descriptionOnDisplay.replaceChildren(
                Text(styles.TEXT, DESCRIPTION_TEXT)
            ) 
        }

    }
   
    const resetEdit = () => {
        const descriptionOnDisplay = document.querySelector('.description-item .text')
        const nameOnDisplay = document.querySelector('.name-item .text')

        nameInput.value = nameOnDisplay.textContent 
        if(descriptionOnDisplay.textContent !== DESCRIPTION_TEXT){
            descriptionInput.value = descriptionOnDisplay.textContent
        }else
            descriptionInput.value = ""
        InfoToast("Undo sucessfull").showToast()
    }

    const modal =  
        Modal("sportEdit","Edit the sport",
            List(styles.DETAILS,
                Item('name-item',
                    Text(styles.DETAIL_HEADER, 'Name: '),
                    nameInput,
                ),
                Item('description-item',
                    Text(styles.DETAIL_HEADER, 'Description: '),
                    descriptionInput ,
                ),
            ),
            Div(styles.ICON_GROUP,
                ButtonIcon(styles.UNDO_ICON, resetEdit, 'Undo'),
                ButtonIcon(styles.CHECK_ICON, onClickConfirm, 'Confirm')
            )
        )

    return modal
}


export {
    DESCRIPTION_TEXT,
    SportEditModal
}