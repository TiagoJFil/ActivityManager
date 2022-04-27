import {List, Item, Text, Button, Div, H1} from '../utils.js'

/**
 * SportList component
 *
 * @param {*} sports sports to display
 * @returns An "ul" element containing a list of "li" elements for each sport
 */
export default function SportList(sports) {
    return Div('header-div',
        H1('header', 'Sports'),
        List('list',
            ...sports.map(sport =>
                Item('resource-item',
                    Button('list-button',
                        () => {
                            location.href = `#sports/${sport.id}`
                        },
                        Text("text",
                            sport.name
                        )
                    )
                )
            )
        )
    )
}