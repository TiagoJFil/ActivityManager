import {List, Item, Text, Button, Icon, Div, H1,Input} from "../dsl.js"
import  {LinkIcon, ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import Modal from "../Modal.js";
import {ErrorToast, InfoToast} from '../../toasts.js'
import {SportEdit, DESCRIPTION_TEXT}  from '../edits/SportEdit.js'


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
    
    const modal = SportEdit(sport,onEditConfirm)

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


