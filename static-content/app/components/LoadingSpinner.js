import {Div} from "./dsl.js"
import styles from "../styles.js"


export default function LoadingSpinner() {


    const loadingElement = Div("loading-container",
        Div(styles.LOADING,
            Div(),
            Div(),
            Div(),
            Div()
        )
    )

    loadingElement.id = "loadingSpinner"

    return loadingElement

}





