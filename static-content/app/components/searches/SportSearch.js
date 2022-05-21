import {Text, Select, Option,Form } from "../dsl.js"
import styles from "../../styles.js";
import SearchBar from "../SearchBar.js";

export default function SportSearch(onSportTextChange, isSelect, withHeader){

    const header = withHeader ? "Search:" : undefined
    
    return Form(styles.SPORT_SEARCH, 
        withHeader ? Text(styles.SEARCH_HEADER, "SPORT") : Text(styles.TEXT, ""),
        SearchBar("searchRes", styles.FORM_TEXT_INPUT, onSportTextChange, "Search for a sport", header),
        isSelect ? Select(styles.FILTER_SELECTOR, "sportSelector", 1, Option(styles.SELECTOR_OPTION , "", "Please search for a sport..."))
        : Text(styles.TEXT, "")
    )
}

