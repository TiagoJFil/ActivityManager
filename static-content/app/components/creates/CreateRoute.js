import {Button, Div, Form, Input, Text} from "../dsl.js"
import styles from "../../styles.js";

/**
 * Creates a form with the new item components.
 *
 * @param onSubmit
 * @returns {HTMLElement}
 */
export default function RouteCreate(onSubmit) {

    const onSubmitForm = () => {
        const startLocation = document.querySelector("#sLocation").value;
        const endLocation = document.querySelector("#eLocation").value;
        const distance = document.querySelector("#distance").value;
        onSubmit(startLocation, endLocation, distance);
    }

    return (
        Form(styles.ADD_ITEM,
            Text(styles.SEARCH_HEADER, "START LOCATION"),
            Input(styles.FORM_TEXT_INPUT, "text", "sLocation", null),
            Text(styles.SEARCH_HEADER, "END LOCATION"),
            Input(styles.FORM_TEXT_INPUT, "text", "eLocation", null),
            Text(styles.SEARCH_HEADER, "DISTANCE"),
            Input(styles.FORM_TEXT_INPUT, "text", "distance", null,'e.g. 10km'),
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Add"))
        )
    )
}