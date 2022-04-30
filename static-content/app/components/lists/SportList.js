import ResourceList from './ResourceList.js';

/**
 * SportList component
 *
 * @param {*} sports sports to display
 * @returns An "ul" element containing a list of "li" elements for each sport
 */
export default function SportList(sports) {
    return ResourceList( 
        sports,
        (sport) => `#sports/${sport.id}`,
        (sport) => sport.name
    )
}