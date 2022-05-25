import {Text, Select, Option,Form} from "../dsl.js"
import styles from "../../styles.js";
import SearchBar  from "../SearchBar.js";

export default function RouteSearch(onLocationsChange,header,defaultSLocation,defaultELocation){

    let currentStartLocation = ""
    let currentEndLocation = ""

    const onStartChange = async (newStartLocation) => {
        currentStartLocation = newStartLocation
        onLocationsChange(currentStartLocation, currentEndLocation)
    }

    const onEndChange = async (newEndLocation) => {
        currentEndLocation = newEndLocation
        onLocationsChange(currentStartLocation, currentEndLocation)
    }

    return Form(styles.ROUTE_SEARCH,
        Text(styles.SEARCH_HEADER, header),
        SearchBar("startLocationDL", styles.FORM_TEXT_INPUT, onStartChange, "Lisbon", "Start Location",defaultSLocation),
        SearchBar("endLocationDL", styles.FORM_TEXT_INPUT, onEndChange, "Porto", "End Location: ",defaultELocation),
        Select(styles.FILTER_SELECTOR, "routeSelector", 1, 
            Option(styles.SELECTOR_OPTION, "", "Please search for locations...")
        )
    )
}