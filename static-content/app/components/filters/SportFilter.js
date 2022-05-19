import {Button, Div, Text} from "../dsl.js"
import styles from "../../styles.js";
import SportSearch from "../searches/SportSearch.js"

export default function SportFilter(onSportTextChange, onSubmit, withHeader){

    const onSubmitForm = () => {
        const searchTextElement = document.querySelector("#searchRes");
        onSubmit(searchTextElement.value);
    }

    return (
        Div(styles.SPORT_FILTER,
            Div(styles.SPORT_SEARCH,
                SportSearch(onSportTextChange, false, withHeader)
            ),
            Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Search"))
        )
    )
}