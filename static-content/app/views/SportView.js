import {List, Item, Text, UserLink, ActivitiesLink, Button} from "./utils.js"


export default function SportView(sport){
    const description = sport.description ?
        Text('description-text', `Description: ${sport.description}`)
        :
        Text('description-text', "No description available")
    return List('sport',
        Item('name-item',
            Text('name-text',
                `Name : ${sport.name}`
            )
        ),
        Item('description-item',
            description
        ),
        Button('activities-link-item',
            `#sports/${sport.id}/activities`,
            'blalbbla'
        ),
        Item('user-link-item',
             UserLink('Created by: ','this user',sport.user)
        )
    )
}
