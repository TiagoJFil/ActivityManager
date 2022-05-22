import {List, Item, Text, Button, Icon, Div, H1,Input} from "../dsl.js"
import  {LinkIcon, ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import Modal from "../Modal.js";
import {ErrorToast, InfoToast} from '../../toasts.js'



const DESCRIPTION_TEXT = "No description available"
/**
 * SportDetails component
 *
 * @param {Sport} sport the sport to display
 *
 * @returns {Div} the sport details component
 */
export default function SportDetails(sport, onEditConfirm) {
    const description = sport.description ?
        [
            Text(styles.DETAIL_HEADER, 'Description: '),
            Text(styles.TEXT, sport.description)
        ]
        : [Text(styles.TEXT, DESCRIPTION_TEXT)]
    

    
    const nameInput = Input('editedName','text', null, null, "e.g Football", sport.name, true)
    const descriptionInput = Input('editedDescription','text', null, null, "e.g Played with a ball", sport.description)

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
        console.log(success)
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
        Modal("Edit the sport",
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

    const onEdit = () => {
        modal.style.display = "flex";
    }

    return List(styles.DETAILS,
            Item('name-item',
                Text(styles.DETAIL_HEADER, 'Name: '),
                Text(styles.TEXT,sport.name)
            ),
            Item('description-item',
                ...description
            ),
            Div(styles.ICON_GROUP,
                LinkIcon(styles.CALENDAR_ICON,`#sports/${sport.id}/activities`, "Get activities for this sport"),
                LinkIcon(styles.USER_ICON, `#users/${sport.user}`, "Get user details"),
                ButtonIcon(styles.EDIT_ICON, onEdit, "Edit sport") 
            ),
            modal
        )
}


