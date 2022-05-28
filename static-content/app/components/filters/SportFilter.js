import {Button, Div, Text} from "../dsl.js"
import styles from "../../styles.js";
import SportSearch from "../searches/SportSearch.js"


/**
 * Creates a sport filter component
 * 
 * @param {Function} onSportTextChange - callback for when the sport text changes
 * @param {Function} onSubmit - callback for when the submit button is pressed
 * @param {Boolean} withHeader - whether to show the header
 */
export default function SportFilter(onSportTextChange, onSubmit, withHeader) {

    const onSubmitForm = () => {
        const searchTextElement = document.querySelector("#searchRes");
        onSubmit(searchTextElement.value);
    }

    return Div(styles.SPORT_FILTER,
            Div(styles.SPORT_SEARCH,
                SportSearch(onSportTextChange, false, withHeader)
            ),
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Search"))
    )

}