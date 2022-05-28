import {Text, Div} from "../dsl.js"
import {ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import Modal from "../Modal.js";


/**
 * Creates a modal dialog to delete an activity.
 * 
 * @param {Function} onConfirm - Function to call when the user confirms the deletion.
 * @return {HTMLElement} - The modal dialog.
 */
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

