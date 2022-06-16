import {List, Item, Text, Div, HidenElem } from "../dsl.js"
import {LinkIcon, ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import {SportEditModal, DESCRIPTION_TEXT}  from '../edits/SportEdit.js'
import {isLoggedIn} from "../../api/session.js";

/**
 * SportDetails component
 *
 * @param {Sport} sport the sport to display
 *
 * @param onEditConfirm callback to call when the edit modal is confirmed
 * @returns {Div} the sport details component
 */
export default function SportDetails(sport, onEditConfirm, isAuthOptionsEnabled) {
    const description = sport.description ?
        [
            Text(styles.DETAIL_HEADER, 'Description: '),
            Text(styles.TEXT, sport.description)
        ]
        : [Text(styles.TEXT, DESCRIPTION_TEXT)]
    
    const modal = SportEditModal(sport,onEditConfirm)

    const onEdit = () => {
        modal.style.display = "flex";
    }
    const addButton =  isLoggedIn() ?  LinkIcon(styles.ADD_ACTIVITY_ICON, `#sports/${sport.id}/activities/add`,"Add an activity") : HidenElem()

    const editButton = isAuthOptionsEnabled ? ButtonIcon(styles.EDIT_ICON, onEdit, "Edit sport") : HidenElem()

    return List(styles.DETAILS,
            Item('name-item',
                Text(styles.DETAIL_HEADER, 'Name: '),
                Text(styles.TEXT,sport.name)
            ),
            Item('description-item',
                ...description
            ),
            Div(styles.ICON_GROUP,
                LinkIcon(styles.USER_ICON, `#users/${sport.user}`, "Get user details"),
                editButton,
                addButton
            ),
            modal
        )
}


