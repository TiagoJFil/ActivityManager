import { Div, Input, Select, Option, Text, Button } from "./dsl.js";
import styles from "../styles.js";

export default function ActivitySearchFilter(onFilterSubmit, routes, query) {

    const onSubmit = () =>{
        const date = document.querySelector('#dateFilter').value
        const route = document.getElementById('rid').value
        const sortOrder = document.getElementById('orderBy').value
        onFilterSubmit(date, route, sortOrder)
    }

    return Div('activity-filter',
        DatePicker(query),
        OrderBySelector(query),
        RouteSelector(routes, query),
        Button('button',onSubmit, Text('filter-text', 'Filter'))
    )

}

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


function RouteSelector(routes, query){
    const initialRouteID = query.rid;

    const noOption = Option(styles.SELECTOR_OPTION, '', Text("no-option-text", "No Route"))
    const routeOptions = routes.map( (route) => {
        return Option(styles.SELECTOR_OPTION, `${route.id}`,
            Text(`${route.id}-text`, `${route.startLocation} - ${route.endLocation}`)
        )
    })
    
    const ridSelector = Select(styles.FILTER_SELECTOR, 'rid',  1,
     ...[noOption, ...routeOptions])

    initialRouteID ? ridSelector.value = initialRouteID : noOption.selected = true

    return ridSelector
}

function DatePicker(query){
    const initialDate = query.date;

    const date = Input('date-filter', 'date', 'dateFilter')
    date.value = initialDate ? initialDate : ''

    return date
}