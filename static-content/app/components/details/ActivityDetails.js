import {Div, H1, HiddenElem, Item, List, Text} from "../dsl.js"
import {ButtonIcon, LinkIcon} from "../Icons.js"
import styles from "../../styles.js";
import ActivityDelete from '../deletes/ActivityDelete.js'
import ActivityEditModal from '../edits/ActivityEditModal.js'

/**
 * ActivityDetails component
 * Contains the details of an activity and its links in form of buttons
 *
 * @param {Activity} activity the activity to display
 * @param onDeleteConfirm the function to call when the user confirms the deletion
 * @param onEditConfirm the function to call when the user confirms the edition
 * @param onRouteChange the function to call when the route changes
 * @param route the current route
 * @param isOwner whether the user is the owner of the activity
 * @returns {HTMLElement} the activity details component
 */
export default function ActivityDetails(activity, route, onDeleteConfirm, onEditConfirm, onRouteChange, isOwner) {
    const hiddenElem = HiddenElem()

    const deleteModal = ActivityDelete(onDeleteConfirm)
    const editModal = ActivityEditModal(activity, onEditConfirm, onRouteChange, route)
    console.log(route)

    const onDeleteClick = () => {
        deleteModal.style.display = "flex";
    }

    const onEdit = () => {
        editModal.style.display = "flex";
    }

    const editButton = isOwner ? ButtonIcon(styles.EDIT_ICON, onEdit, "Edit activity", "Edit-button") : hiddenElem
    const deleteButton = isOwner ? ButtonIcon(styles.TRASH_ICON, onDeleteClick, "Delete Activity") : hiddenElem

    deleteButton.classList.add(styles.DELETE_ICON)

    return Div(styles.HEADER_DIV,
        H1(styles.HEADER, 'Activity Details'),
        List(styles.DETAILS,
            Item('date-item',
                Text(styles.DETAIL_HEADER, 'Date: '),
                Text(styles.TEXT, activity.date)
            ),
            Item('duration-item',
                Text(styles.DETAIL_HEADER, 'Duration: '),
                Text(styles.TEXT, activity.duration)
            ),
            Div(styles.ICON_GROUP,
                LinkIcon(styles.SPORT_ICON, `#sports/${activity.sport}`, "Get Sport Details"),
                LinkIcon(styles.USER_ICON, `#users/${activity.user}`, "Get User Details"),
                editButton,
                activity.route
                    ? LinkIcon(styles.ROUTE_ICON, `#routes/${activity.route}`, "Get Route Details", "route-link") : hiddenElem, // Route Link if route exists
                activity.route
                    ? LinkIcon(styles.USERS_ICON, `#sports/${activity.sport}/users?rid=${activity.route}`, "Get users that have similar activities") : hiddenElem,  // Users Link if route exists
                deleteButton
            )
        ),
        deleteModal,
        editModal
    )
}
