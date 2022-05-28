import {Button, Form, Input, Text} from "../dsl.js"
import styles from "../../styles.js";
import { DatePicker } from "../filters/ActivitySearchFilter.js";
import RouteSearch from "../searches/RouteSearch.js";


/**
 * Creates a form with the new item components.
 *
 * @param onSubmit - The function to call when the form is submitted.
 * @param onRouteChange - function to be called when the route inputted is changed
 * @returns {HTMLElement} - The form element.
 */
export default function ActivityCreate(onSubmit, onRouteChange) {

    const datePicker = DatePicker()
    const routeSearch =  RouteSearch(onRouteChange,"Select a route")

    const onSubmitForm = () => {
        const duration = document.querySelector("#duration").value;
        const routeId = document.querySelector('#routeSelector').value
        onSubmit(datePicker.value, duration, routeId);
    }

    return (
        Form(styles.ADD_ITEM,
            Text(styles.SEARCH_HEADER, "DATE"),
            datePicker,
            Text(styles.SEARCH_HEADER, "DURATION"),
            Input(styles.FORM_TEXT_INPUT, "text", "duration", null),
            routeSearch,
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Add"))
        )
    )
}