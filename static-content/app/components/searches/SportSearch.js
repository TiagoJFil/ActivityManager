import {Form, Option, Select, Text} from "../dsl.js"
import styles from "../../styles.js";
import SearchBar from "../SearchBar.js";


/**
 * Creates the SportSearch component.
 *
 * @param {Function} onSportTextChange - The function to call when the sport text changes.
 * @param {Boolean} isSelect - Whether or not a select element should be used.
 * @param {String} header -The header to use.
 */
export default function SportSearch(onSportTextChange, isSelect, header){


    return Form(styles.SPORT_SEARCH,
        Text(styles.SEARCH_HEADER, header),
        SearchBar("searchRes", styles.FORM_TEXT_INPUT,
            onSportTextChange,
            "i.e Badminton",
            "Search:",
            null
        ),
        isSelect ?
            Select(styles.FILTER_SELECTOR,
                "sportSelector",
                1,
                Option(styles.SELECTOR_OPTION, "", "Please search for a sport...")
            )
            : Text(styles.TEXT, "")
    )
}

