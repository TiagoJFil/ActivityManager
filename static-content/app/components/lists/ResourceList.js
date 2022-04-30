import {Button, List, Item, Text}  from "../dsl.js"
import styles from "../../styles.js"

/**
 * 
 * @param {Array<*>} resources  the resources to display
 * @param {Function} hrefSupplier a function that returns the href for a resource details. Receives the resource as parameter.
 * @param {Function} displayTextSupplier a function that returns the text to display for a resource. Receives the resource as parameter.
 * @returns 
 */
export default function ResourceList(resources, hrefSupplier,  displayTextSupplier) {

    if(resources.length === 0) {
        return Text(styles.NO_RESULTS_TEXT, 'No results found')
    }
        

    return List(styles.LIST,
        ...resources.map(resource =>
            Item(styles.LIST_ELEMENT,
                Button(styles.LIST_BUTTON, () => {location.href = hrefSupplier(resource)},
                    Text(styles.TEXT, displayTextSupplier(resource))
                )
            )
        )
    )
}