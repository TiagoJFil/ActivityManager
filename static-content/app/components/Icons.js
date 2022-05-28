import {Button, Icon,Anchor} from "./dsl.js"
import styles from "../styles.js"

/**
 * Creates a button with an icon.
 * When clicked, the button navigates to the given href.
 *
 * @param className the className to apply to the button
 * @param {String} href the href to set on the button
 * @param titleOnHover the title to set on the button when hovered
 * @param id the id to set on the button
 * @returns {HTMLElement} the button
 */
export function LinkIcon(className, href ,titleOnHover, id){
    const button = Button(styles.ICON_BUTTON_CLASS,
        () => {
            window.location.href = href
        },
        BoxIcon(className)
    )
    id ? button.id = id : null
    button.title = titleOnHover || href
    return button
}

/**
 * Creates a button with an icon.
 * When clicked, the button executes the given onClick function.
 *
 * @param className the className to apply to the button
 * @param onClick the function to execute when the button is clicked
 * @param titleOnHover the title to set on the button when hovered
 * @param id the id to set on the button
 * @returns {HTMLElement} the button
 */
export function ButtonIcon(className, onClick, titleOnHover,id){
    const button = Button(styles.ICON_BUTTON_CLASS, onClick,
        BoxIcon(className)
    )
    id ? button.id = id : null
    button.title = titleOnHover
    return button
}

/**
 * Creates a button with an icon.
 * When clicked this button opens a new separator with the given href.
 * 
 * @param {String} className  the className to apply to the button
 * @param {String} href  the href to set on the button
 * @returns  {Button} the button
 */
export function OuterLinkIcon(className, href){
    return Button(styles.OUTER_BUTTON_CLASS,
        () => {
            window.open(href, '_blank')
        },
        BoxIcon(className)
    )
}


/**
 * Creates a borderless icon button
 * @param {String} href 
 * @param {String} titleOnHover 
 * @returns {Anchor} the anchor 
 */
export function BoardlessIconButton(href,titleOnHover){
    const addAnchor = Anchor(styles.BIG,href, BoxIcon(styles.ADD_ICON) )
    addAnchor.title = titleOnHover
    return addAnchor
}

/**
 * Creates an icon with the given class
 */
export function BoxIcon(className){
    return Icon(styles.BX_CLASS, className)
}

