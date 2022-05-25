import {List, Item, Text, Div, Input, TextArea} from "../dsl.js"
import  {ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import Modal from "../Modal.js";
import {ErrorToast, InfoToast} from '../../toasts.js'


export default function ActivityDelete(onConfirm){

    const cancelDelete = () => {
        modal.style.display = 'none'
    }

    const modal =  
        Modal('delete',"Confirm",
            Text(styles.TEXT, "Are you sure you want to delete this activity?"),
            Div(styles.ICON_GROUP,
                ButtonIcon(styles.UNDO_ICON, cancelDelete, 'Cancel'),
                ButtonIcon(styles.CHECK_ICON, onConfirm, 'Confirm')
            )
        )
    
        

    return modal
}

