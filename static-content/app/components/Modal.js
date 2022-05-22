import {Button, Form, Icon, Div,Text} from "./dsl.js"
import  {ButtonIcon} from "./Icons.js"
import styles from "../styles.js";

/**
 *  Represents a modal window component.
 */
export default function Modal(headerText,...children){

    const onDispose = () => {
        modal.style.display = 'none'
    }
    const button = Button(styles.UNSTYLED_BUTTON, onDispose,
        Icon(styles.BX_CLASS,styles.CLOSE_ICON)
        )
    button.title = 'Close'
    const modal = Div(styles.MODAL_CONTAINER,
            Div(styles.MODAL,
                Div(styles.MODAL_HEADER,
                    Text(styles.MODAL_HEADER_TEXT, headerText),

                    button,
                ),
                Div(styles.MODAL_CONTENT,
                    ...children
                ),
            )
        )
    
    modal.style.display = 'none'
    return modal
}