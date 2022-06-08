import {Button, Form, Input, Text} from "../dsl.js"
import styles from "../../styles.js";

/**
 * Creates a form with the new item components.
 *
 * @param onSubmit - The function to call when the form is submitted.
 * @returns {HTMLElement} - The form element.
 */
export default function CreateUser(onSubmit) {

    const onSubmitForm = () => {
        const userName = document.querySelector("#name").value
        const email = document.querySelector("#email").value;
        const password = document.querySelector("#password").value;
        const checkPassword = document.querySelector("#checkPassword").value;
        onSubmit(userName, email, password, checkPassword);
    }

    return Form(styles.ADD_ITEM,
            Text(styles.SEARCH_HEADER, "NAME"),
            Input(styles.FORM_TEXT_INPUT, "text", "name", null),
            Text(styles.SEARCH_HEADER, "EMAIL"),
            Input(styles.FORM_TEXT_INPUT, "text", "email", null),
            Text(styles.SEARCH_HEADER, "PASSWORD"),
            Input(styles.FORM_TEXT_INPUT, "password", "password", null),
            Text(styles.SEARCH_HEADER, "REINSERT PASSWORD"),
            Input(styles.FORM_TEXT_INPUT, "password", "checkPassword", null),
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Register"))
        )
    
}