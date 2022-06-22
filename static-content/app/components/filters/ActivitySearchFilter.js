import {Button, Div, Input, Option, Select, Text} from "../dsl.js";
import styles from "../../styles.js";
import RouteSearch from "../searches/RouteSearch.js";
import SportSearch from "../searches/SportSearch.js";


/**
 * Creates a div with the filter components.
 *
 * @param {Function} onFilterSubmit
 * @param {Function} onRouteChange
 * @param {Function} onSportChange
 * @param {Object} query
 * @returns {HTMLElement} component with the filter components.
 */
export default function ActivitySearchFilter(onFilterSubmit, onRouteChange, onSportChange, query) {

    const onSubmit = () => {
        const dateElem = document.querySelector('#dateFilter')
        const sportElem = document.querySelector('#sportSelector')
        const routeElem = document.querySelector('#routeSelector')
        const sortOrderElem = document.querySelector('#orderBy')

        const date = dateElem.value
        const sid = sportElem.value
        const rid = routeElem.value
        const sortOrder = sortOrderElem.value
        onFilterSubmit(date, sid, rid, sortOrder)
    }


    return Div("activity-filter-container",
        Div(styles.ACTIVITY_FILTER,
            Div(styles.FILTERS_CONTAINER,
                Text(styles.SEARCH_HEADER, "DATE"),
                DatePicker(null),
                Text(styles.SEARCH_HEADER, "DURATION Sorted by:"),
                OrderBySelector(query),
            ),
            Div(styles.SEARCHES_CONTAINER,
                SportSearch(onSportChange, true, "SPORT"),
                RouteSearch(onRouteChange, "ROUTE"),
            ),
        ),
        Button(styles.BUTTON, onSubmit, Text('filter-text', 'Filter'))
    )

}

/**
 * Creates a Select component with options for sorting the list of activities.
 * Selects the option that matches the query.orderBy value.
 * @param {Object} query 
 * @returns {Select} the Select component created.
 */
function OrderBySelector(query){
    const initialOrder = query.orderBy ;

    const ascendingOption = 
        Option(styles.SELECTOR_OPTION, 'ascending','Ascending',
            Text(styles.TEXT,'Ascending')
        )
    const descendingOption = 
        Option(styles.SELECTOR_OPTION, 'descending','Descending',
            Text(styles.TEXT, 'Descending')
        )

    const orderBySelector 
        = Select(styles.FILTER_SELECTOR, 'orderBy', 1,
            ascendingOption,
            descendingOption
        )

    if(!initialOrder || initialOrder === 'ascending'){
        orderBySelector.value = 'ascending'
    }else{
        orderBySelector.value = 'descending'
    }

    return orderBySelector   
}


/**
 * Creates a DatePicker component.
 * Inserts the value of the query.date property into the input field.
 * @returns {Input} the DatePicker component created.
 * @param initialDate the initial value of the date field.
 */
export function DatePicker(initialDate){
    const date = Input('date-filter', 'date', 'dateFilter')
    date.value = initialDate ? initialDate : ''
    return date
}