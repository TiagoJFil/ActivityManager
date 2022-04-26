import {List, Item, Text ,UserLink} from "./utils.js"

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
        Item('user-link-item',
             UserLink('Created by: ','this user',sport.user)
        )
    )
}
