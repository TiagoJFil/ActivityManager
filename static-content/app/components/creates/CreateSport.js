import {Button, Div, Form, Input, Text} from "../dsl.js"
import styles from "../../styles.js";

/**
 * Creates a form with the new item components.
 *
 * @param onSubmit
 * @returns {HTMLElement}
 */
export default function SportCreate(onSubmit) {

    const onSubmitForm = () => {
        const sportName = document.querySelector("#sportName");
        const description = document.querySelector("#sportDescription");
        onSubmit(sportName.value, description.value);
    }

    return (
        Form(styles.ADD_ITEM,
            Text(styles.SEARCH_HEADER, "NAME"),
            Input(styles.FORM_TEXT_INPUT, "text", "sportName", null),
            Text(styles.SEARCH_HEADER, "DESCRIPTION"),
            Input(styles.FORM_TEXT_INPUT, "text", "sportDescription", null, "Optional"),
            Div(styles.ADD_BUTTON_CONTAINER,
                Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Add"))
            )
        )
    )
}