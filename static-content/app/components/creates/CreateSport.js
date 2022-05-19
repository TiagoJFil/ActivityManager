import {Button, Div, Form, Input, Option, Select, Text} from "../dsl.js"
import styles from "../../styles.js";

/**
 * Creates a form with the new item components.
 *
 * @param onSubmit
 * @returns {HTMLElement}
 */
export default function SportCreate(onSubmit) {

    const onSubmitForm = () => {
        const sportName = document.getElementById("sport-name");
        console.log(sportName.value)
        const description = document.getElementById("sport-description");
        console.log(description.value)
        onSubmit(sportName.value, description.value);
    }

    return (
        Form(styles.ADD_ITEM,
            Text(styles.SEARCH_HEADER, "NAME"),
            Input(styles.FORM_TEXT_INPUT, "text", "sport-name"),
            Text(styles.SEARCH_HEADER, "DESCRIPTION"),
            Input(styles.FORM_TEXT_INPUT, "text", "sport-description"),
            Div(styles.ADD_BUTTON_CONTAINER,
                Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Add")
                )
            )
        )
    )
}