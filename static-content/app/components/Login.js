import {Button, Form, Input, Text} from "./dsl.js"
import styles from "../styles.js";

/**
 * Returns the login form component.
 *
 * @param onSubmit - The function to call when the form is submitted.
 * @returns {HTMLElement} - The form element.
 */
export default function Login(onSubmit) {
    
    
    const onSubmitForm = () => {
        const email = document.querySelector("#email").value;
        const password = document.querySelector("#password").value;

        onSubmit(email, password, submitButton)
    }

    const submitButton = Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Log in"))

    return (
        Form(styles.LOGIN_ITEM,
            Text(styles.SEARCH_HEADER, "EMAIL"),
            Input(styles.FORM_TEXT_INPUT, "text", "email"),
            Text(styles.SEARCH_HEADER, "PASSWORD"),
            Input(styles.FORM_TEXT_INPUT, "password", "password"),
            submitButton
        )
    )

}