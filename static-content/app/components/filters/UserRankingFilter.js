import {Button, Div, Form, Text} from "../dsl.js"
import styles from "../../styles.js";
import RouteSearch from "../searches/RouteSearch.js";
import SportSearch from "../searches/SportSearch.js"


/**
 * Creates the User Ranking Filter component
 * 
 * @param {Function} onSportTextChange - callback function for when the sport text changes
 * @param {Function} onLocationsChange - callback function for when the route location text changes
 * @param {Function} onSubmit - callback function for when the form is submitted
 */
export default function UserRankingFilter(onSportTextChange, onLocationsChange, onSubmit){

        const onSubmitForm = () => {

            const routeSelector = document.querySelector("#routeSelector");
            const sportSelector = document.querySelector("#sportSelector");

            const routeID = routeSelector.value;
            const sportID = sportSelector.value;

            onSubmit(routeID, sportID);
        }

        const withHeader = true
        return (
            Form(styles.USER_RANKINGS,
                Div(styles.RANKINGS_FORM,
                    RouteSearch(onLocationsChange,"ROUTE"),
                    SportSearch(onSportTextChange, true, withHeader),
                ),
                Button(styles.BUTTON, onSubmitForm, Text(styles.TEXT, "Search"))
            )
        )
}