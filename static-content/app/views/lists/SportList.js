import { List, Item, Anchor, Text } from '../utils.js'

/**
 * SportList component
 * 
 * @param {*} sports sports to display
 * @returns An "ul" element containing a list of "li" elements for each sport
 */
export default function SportList(sports){
    return List('sport-list',
        ...sports.map(sport => 
            Item( 'sport', 
                Anchor('link', `#sports/${sport.id}`,
                    Text("text",
                        sport.name
                    )
                )
            )
        )
    )

}