import {routeApi, sportApi} from '../api/api.js'
import {Option} from '../components/dsl.js'
import styles from '../styles.js'
import {isLoggedIn} from "../api/session.js"
import { Navigation } from "../components/Navigation.js";

export const DURATION_REGEX = /^(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]$/

/**
 * Auxiliary function to deal with input change events of route
 * Replaces Route's selector with routes that are relevant to both of the locations
 */
 export async function onRouteLocationsChange(startLocation, endLocation) {
   const routeSelector = document.querySelector("#routeSelector")
   
   if(startLocation.length < 1 && endLocation.length < 1) {
       routeSelector.replaceChildren(
           Option(styles.SELECTOR_OPTION , "", "Please search for a locations...")
       )
       return
   }

   const newRouteQuery = {
       startLocation,
       endLocation
   }

   const routeList = await routeApi.fetchRoutes(newRouteQuery)

   const routeSelectorOptions = routeList.routes.map( (route) =>
       Option(styles.SELECTOR_OPTION, `${route.id}`,
           `${route.startLocation} - ${route.endLocation}`
        )
   )
   
   routeSelector.replaceChildren(...routeSelectorOptions)
}

/**
* Auxiliary function to deal with input change events of sport
* Replaces Sport's selector with sports that are relevant to the typed text
*/
export async function onSportTextChange (sportText){
   const sportSelector = document.querySelector("#sportSelector")
   
   if(sportText < 1){
       sportSelector.replaceChildren(
           Option(styles.SELECTOR_OPTION , "", "Please search for a sport...")
       )
       return
   }

   const sportList = await sportApi.fetchSports({search : sportText})
   console.log(sportList)
   const sportSelectorOptions = sportList.sports.map(sport => Option(styles.SELECTOR_OPTION, sport.id, sport.name))

   sportSelector.replaceChildren(
       ...sportSelectorOptions
   )
}


export function reloadNav(){
    document.querySelector(".main-nav").replaceChildren(
        Navigation(isLoggedIn()),
    )
}
