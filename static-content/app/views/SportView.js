import {List, Item, Text, UserLink, ActivitiesLink} from "./utils.js"


export default function SportView(sport){
    return List('sport',
        Item('name-item',
            Text('name-text',
                `Name : ${sport.name}`
            )
        ),
        Item('description-item',
            Text('description-text',
                `Description: ${sport.description}`
            )
        ),
        Item('activities-link-item',
            ActivitiesLink('this activities',sport.id)
        ),
        Item('user-link-item',
             UserLink('Created by: ','this user',sport.user)
        )
    )
}
