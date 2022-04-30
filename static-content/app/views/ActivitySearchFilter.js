import { Div, Input, Select, Option, Text, Button } from "./utils.js";
import styles from "../styles.js";
//TODO(arranjar uma forma de resetar a date quando o utilizador desejar)
export default function ActivityFilter(onFilterSubmit, routes) {

    const onSubmit = () =>{
        const date = document.querySelector('#dateFilter').value
        const route = document.getElementById('rid').value
        const sortOrder = document.getElementById('orderBy').value
        onFilterSubmit(date, route, sortOrder)
    }

    const orderBySelector 
    = Select('orderBy-selector', 'orderBy', 1,
        Option(styles.SELECTOR_OPTION, 'ascending',
            Text(styles.TEXT,'Ascending')
        ),
        Option(styles.SELECTOR_OPTION,'descending',
            Text(styles.TEXT,'Descending')
        )
    )

    const routeOptions = routes.map( (route) => {
        return Option(styles.SELECTOR_OPTION, `${route.id}`,
            Text(`${route.id}-text`, `${route.startLocation} - ${route.endLocation}`)
        )}
    )

    const noOption = Option(styles.SELECTOR_OPTION, '', Text("no-option-text", "No Route"))
    noOption.selected = true

    const ridSelector = Select('rid-selector', 'rid',  1,
     ...[noOption, ...routeOptions])

    return Div('activity-filter',
        Input('date-filter', 'date', 'dateFilter'),
        orderBySelector,
        ridSelector,
        Button('button',onSubmit, Text('filter-text', 'Filter'))
    )
}