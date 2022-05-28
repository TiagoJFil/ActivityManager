import {Button, Div, Text} from "../dsl.js"
import styles from "../../styles.js";
import RouteSearch from "../searches/RouteSearch.js"


/**
 * Creates a route filter component.
 * 
 * @param {RouteFilter} onSubmit - Callback for when the filter is submitted.
 */
export default function RouteFilter(onSubmit){

    const onSubmitForm = () => {
        const sLocation = document.getElementById('startLocation').value
        const eLocation = document.getElementById('EndLocation').value
        onSubmit(sLocation, eLocation)
    }

    return (
        Div(styles.ROUTE_FILTER,
            Div(styles.ROUTE_SEARCH,
                RouteSearch()
            ),
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Search"))
        )
    )
}