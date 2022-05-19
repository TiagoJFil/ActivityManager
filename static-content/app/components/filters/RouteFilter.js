import {Button, Div, Text} from "../dsl.js"
import styles from "../../styles.js";
import RouteSearch from "../searches/RouteSearch.js"

export default function RouteFilter(onSubmit){

    const onSubmitForm = () => {


        onSubmit(searchText);
    }

    return (
        Div("route-filter", 
            Div("route-search",
                RouteSearch()
            ),
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Search"))
        )
    )
}