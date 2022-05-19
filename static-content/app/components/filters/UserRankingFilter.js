import {Button, Div, Text} from "../dsl.js"
import styles from "../../styles.js";
import RouteSearch from "../searches/RouteSearch.js";
import SportSearch from "../searches/SportSearch.js"

export default function UserRankingFilter(onSportTextChange, onSubmit){

        const onSubmitForm = () => {

            const routeSelector = document.querySelector("#routeSelector");
            const sportSelector = document.querySelector("#sportSelector");

            const routeID = routeSelector.value;
            const sportID = sportSelector.value;

            onSubmit(routeID, sportID);
        }
        const withHeader = true
        return (
            Div(styles.USER_RANKINGS,
                Div(styles.RANKINGS_FORM,
                    RouteSearch(),
                    SportSearch(onSportTextChange, true, withHeader),
                ),
                Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Search"))
            )
        )
}