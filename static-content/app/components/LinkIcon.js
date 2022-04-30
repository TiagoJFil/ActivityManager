import {Button, Icon}  from "./dsl.js"
import styles from "../styles.js"


export function LinkIcon(className, href){
    return Button(styles.ICON_BUTTON_CLASS,
        () => {
            location.href = href
        },
        Icon(styles.BX_CLASS, className)
    )
}

export function OuterLinkIcon(className, href){
    return Button(styles.OUTER_BUTTON_CLASS,
        () => {
            window.open(href, '_blank')
        },
        Icon(styles.BX_CLASS, className)
    )
}