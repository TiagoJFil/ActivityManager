import {Button, Form, Input, Text} from "./dsl.js"
import styles from "../styles.js";

/**
 * Creates a form with the new item components.
 *
 * @param onSubmit - The function to call when the form is submitted.
 * @returns {HTMLElement} - The form element.
 */
export default function Login(onSubmit) {

    const onSubmitForm = () => {
        const email = document.querySelector("#email").value;
        const password = document.querySelector("#password").value;
        onSubmit(email, password);
    }

    return (
        Form(styles.LOGIN_ITEM,
            Text(styles.SEARCH_HEADER, "EMAIL"),
            Input(styles.FORM_TEXT_INPUT, "text", "email", null),
            Text(styles.SEARCH_HEADER, "PASSWORD"),
            Input(styles.FORM_TEXT_INPUT, "password", "password", null),
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Log in"))
        )
    )
}