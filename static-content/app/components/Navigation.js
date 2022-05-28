import {Anchor, Nav, Image, List,Item, Text, Div} from "./dsl.js"
import styles from "../styles.js"
import {BoxIcon} from "./Icons.js";


/**
 * The navigation bar component
 */
export function Navigation(){

    return Nav(
        Anchor(styles.LOGO, "#home",
            Image(styles.LOGO_IMAGE, null, "img/logo.png")
        ),
        List(styles.NAV_LINKS,
            Item(null,
                Anchor(null, "#home", 
                    BoxIcon(styles.HOME_ICON),
                    Text(styles.NAV_TEXT, "Home")
                    )
                ),   
            Item(null,
                Anchor(null, "#users/ranking", 
                    BoxIcon(styles.USERS_ICON),
                    Text(styles.NAV_TEXT, "UserRanking")
                    )
                ),
            Item(null,
                Anchor(null, "#sports?limit=20&skip=0", 
                    BoxIcon(styles.SPORT_ICON),
                    Text(styles.NAV_TEXT, "Sports")
                    )
                ),
            Item(null,
                Anchor(null, "#routes?limit=20&skip=0",
                    BoxIcon(styles.ROUTE_ICON),
                    Text(styles.NAV_TEXT, "Routes")
                    )
                ),
            Item(null,
                Anchor(null, "#activities/search", 
                    BoxIcon(styles.CALENDAR_ICON),
                    Text(styles.NAV_TEXT, "Activities")
                )
            ),
            Div(styles.ACTIVE)
        )
    )

}

