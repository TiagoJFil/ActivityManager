import { Div, Input, Select, Option, Text, Button } from "../dsl.js";
import styles from "../../styles.js";
import RouteSearch from "../searches/RouteSearch.js";

/**
 * Creates a div with the filter components.
 * 
 * @param {Function} onFilterSubmit 
 * @param {array<*>} routes 
 * @param {Object} query 
 * @returns a {Div} component with the filter components.
 */
export default function ActivitySearchFilter(onFilterSubmit, routes, query) {

    const onSubmit = () => {
        const date = document.querySelector('#dateFilter').value
        const route = document.querySelector('#rid').value
        const sortOrder = document.querySelector('#orderBy').value
        onFilterSubmit(date, route, sortOrder)
    }

    return Div(styles.ACTIVITY_FILTER,
        DatePicker(query),
        OrderBySelector(query),
        RouteSelector(routes, query),
        Button('button',onSubmit, Text('filter-text', 'Filter'))
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
        Option(styles.SELECTOR_OPTION, 'ascending',
            Text(styles.TEXT,'Ascending')
        )
    const descendingOption = 
        Option(styles.SELECTOR_OPTION, 'descending',
            Text(styles.TEXT,'Descending')
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
 * Creates a Select component with options for selecting a route.
 * Selects the option that matches the query.route value.
 * @param {array<*>} routes 
 * @param {Object} query 
 * @returns {Select} the Select component created.
 */
export function RouteSelector(routes, query){
    const initialRouteID = query.rid;

    const noOption = Option(styles.SELECTOR_OPTION, '', "No Route")
    const routeOptions = routes.map( (route) => {
        return Option(styles.SELECTOR_OPTION, `${route.id}`,
            `${route.startLocation} - ${route.endLocation}`
        )
    })
    
    const ridSelector = Select(styles.FILTER_SELECTOR, 'rid',  1,
     ...[noOption, ...routeOptions])

    initialRouteID ? ridSelector.value = initialRouteID : noOption.selected = true

    return ridSelector
}

/**
 * Creates a DatePicker component.
 * Inserts the value of the query.date property into the input field.
 * @param {Object} query 
 * @returns {Input} the DatePicker component created.
 */
function DatePicker(query){
    const initialDate = query.date;

    const date = Input('date-filter', 'date', 'dateFilter')
    date.value = initialDate ? initialDate : ''

    return date
}