import {List, Item, Text, Div, H1} from "../dsl.js"
import {LinkIcon,ButtonIcon} from "../Icons.js"
import styles from "../../styles.js";
import ActivityDelete  from '../deletes/ActivityDelete.js'
import ActivityEdit  from '../edits/ActivityEdit.js'

/**
 * ActivityDetails component
 * Contains the details of an activity and its links in form of buttons
 *
 * @param {Activity} activity the activity to display
 * @param onDeleteConfirm the function to call when the user confirms the deletion
 * @param onEditConfirm the function to call when the user confirms the edition
 * @param onRouteChange the function to call when the user ?????????????????????? Não deveria estar no onEditConfirm? TODO?
 * @param route the current route
 * @returns {HTMLElement} the activity details component
 */
export default function ActivityDetails(activity, onDeleteConfirm, onEditConfirm, onRouteChange, route) {
    const emptyText = Text(styles.TEXT, '')

    const deleteModal = ActivityDelete(onDeleteConfirm)

    const editModal = ActivityEdit(activity, onEditConfirm, onRouteChange, route)

    const onDeleteClick = () => {
        deleteModal.style.display = "flex";
    }

    const onEdit = () => {
        editModal.style.display = "flex";
    }

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
                LinkIcon(styles.SPORT_ICON,`#sports/${activity.sport}`,"Get Sport Details"),
                LinkIcon(styles.USER_ICON, `#users/${activity.user}`, "Get User Details"),
                ButtonIcon(styles.TRASH_ICON, onDeleteClick, "Delete Activity"),
                ButtonIcon(styles.EDIT_ICON, onEdit, "Edit activity", "Edit-button"),
                activity.route 
                ? LinkIcon(styles.ROUTE_ICON,`#routes/${activity.route}`,"Get Route Details", "route-link") : emptyText, // Route Link if route exists
                activity.route 
                ? LinkIcon(styles.USERS_ICON, `#sports/${activity.sport}/users?rid=${activity.route}`, "Get users by rid and sid") : emptyText  // Users Link if route exists
            )
        ),
        deleteModal,
        editModal
    )
}
