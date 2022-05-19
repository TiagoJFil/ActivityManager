import {Input, Div,Text, Select, Option,Form} from "../dsl.js"
import {routeApi} from '../../api.js'
import styles from "../../styles.js";




export default function RouteSearch(){

    let currentStartLocation = ""
    let currentEndLocation = ""

    const onLocationsChange = async (startLocation, endLocation) =>{
        const routeSelector = document.querySelector("#routeSelector")

        if(startLocation.length < 3 && endLocation.length < 3) {
            routeSelector.replaceChildren()
            return
        }

        const routes = await routeApi.fetchRoutes(`startLocation=${startLocation}&endLocation=${endLocation}`)
        const routeOptions = routes.map( (route) => {

            return Option(styles.SELECTOR_OPTION, `${route.id}`,
                `${route.startLocation} - ${route.endLocation}`
             )
        })
        
        routeSelector.replaceChildren(...routeOptions)
    } 

    const onStartChange = async (startInputEvent) => {
        currentStartLocation = startInputEvent.target.value
        await onLocationsChange(currentStartLocation, currentEndLocation)
    }

    const onEndChange = async (endInputEvent) => {
        currentEndLocation = endInputEvent.target.value
        await onLocationsChange(currentStartLocation, currentEndLocation)
    }

    return Form(styles.ROUTE_SEARCH,
        Text(styles.SEARCH_HEADER, "ROUTE"),
        Text(styles.DETAIL_HEADER, "Start Location: "),
        Input(styles.FORM_TEXT_INPUT, "text", "startLocation", onStartChange,"startLocationDL"),
        Text(styles.DETAIL_HEADER, "End Location: "),
        Input(styles.FORM_TEXT_INPUT, "text", "EndLocation", onEndChange,"endLocationDL"),
        Select(styles.FILTER_SELECTOR, "routeSelector", 1, 
            Option(styles.SELECTOR_OPTION, "0", "Please search for locations...")
        )
    )
}