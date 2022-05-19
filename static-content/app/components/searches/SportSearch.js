import {Input, Text, Select, Option,Form , Datalist} from "../dsl.js"
import styles from "../../styles.js";

export default function SportSearch(onSportTextChange, isSelect, withHeader){

    let currentSportText = ""

    const onSearchChange = async (startInputEvent) => {
        currentSportText = startInputEvent.target.value
        await onSportTextChange(currentSportText)
    }
    
    return Form(styles.SPORT_SEARCH, 
        withHeader ? Text(styles.SEARCH_HEADER, "SPORT") : Text(styles.TEXT, ""),
        withHeader ? Text(styles.DETAIL_HEADER, "Search: ") : Text(styles.TEXT, ""),
        Input(styles.FORM_TEXT_INPUT, "text", "searchRes", onSearchChange, "sportSearchDL"),
        isSelect ? Select(styles.FILTER_SELECTOR, "sportSelector", 1, Option(styles.SELECTOR_OPTION , "none", "Please search for a sport..."))
        : Text(styles.TEXT, "")
    )
}

