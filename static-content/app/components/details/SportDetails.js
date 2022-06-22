import {Div, HiddenElem, Item, List, Text} from "../dsl.js"
import {ButtonIcon, LinkIcon} from "../Icons.js"
import styles from "../../styles.js";
import {DESCRIPTION_TEXT, SportEditModal} from '../edits/SportEditModal.js'
import {isLoggedIn} from "../../api/session.js";

/**
 * SportDetails component
 *
 * @param {Sport} sport the sport to display
 *
 * @param onEditConfirm callback to call when the edit modal is confirmed
 * @param isOwner
 * @returns {Div} the sport details component
 */
export default function SportDetails(sport, onEditConfirm, isOwner) {
    const description = sport.description ?
        [
            Text(styles.DETAIL_HEADER, 'Description: '),
            Text(styles.TEXT, sport.description)
        ]
        : [Text(styles.TEXT, DESCRIPTION_TEXT)]

    const modal = SportEditModal(sport, onEditConfirm)

    const onEdit = () => {
        modal.style.display = "flex";
    }
    const addButton = isLoggedIn() ? LinkIcon(styles.ADD_ACTIVITY_ICON, `#sports/${sport.id}/activities/add`, "Add an activity") : HiddenElem()

    const editButton = isOwner ? ButtonIcon(styles.EDIT_ICON, onEdit, "Edit sport") : HiddenElem()

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


