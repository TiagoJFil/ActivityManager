import { List, Item } from './utils.js'

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
                sport.name.linkTo(`#sports/${sport.id}`)
            )
        )
    )

}