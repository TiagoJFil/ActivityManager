import {Button, Form, Icon} from "./dsl.js"
import styles from "../styles.js"

/**
 * Creates a button with an icon.
 * @param className the className to apply to the button
 * @param {String} href the href to set on the button
 * @param titleOnHover the title to set on the button when hovered
 * @returns {HTMLElement} the button
 */
export function LinkIcon(className, href ,titleOnHover  ){
    const button = Button(styles.ICON_BUTTON_CLASS,
        () => {
            location.href = href
        },

        Icon(styles.BX_CLASS, className)
    )
    button.title = titleOnHover || href
    return button
}


export function ButtonIcon(className, onClick, titleOnHover){
    const button = Button(styles.ICON_BUTTON_CLASS,
        onClick,
        Icon(styles.BX_CLASS,className)
    )
    button.title = titleOnHover
    return button
}

/**
 * Creates a button with an icon.
 * When clicked this button opens a new page with the given href.
 * @param {String} className  the className to apply to the button
 * @param {String} href  the href to set on the button
 * @returns  {Button} the button
 */
export function OuterLinkIcon(className, href){
    return Button(styles.OUTER_BUTTON_CLASS,
        () => {
            window.open(href, '_blank')
        },
        Icon(styles.BX_CLASS, className)
    )
}
