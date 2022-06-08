import {Div, Input, Text} from "./dsl.js";
import styles from "../styles.js";
import { debounce } from "../handlers/utils.js"

/**
 * Search bar component
 * 
 * @param id {string} - The id of the search bar
 * @param className {string} - The class name of the search bar
 * @param onTextChange {function} - The function to call when the text changes
 * @param placeholder {string} - The placeholder text
 * @param header {string} - The header of the search bar or none if not needed
 * @returns {HTMLElement}
 */
export default function SearchBar(id, className, onTextChange, placeholder, header, defaultValue) {
    
    const onChange = (event) => {
        onTextChange(event.target.value)
    };

    const debouncedChange = debounce(onChange, 200);

    const searchBar = Input(className, "search", id, debouncedChange, placeholder, defaultValue);
    const items = header
        ? [Text(styles.DETAIL_HEADER, header), searchBar]
        : [searchBar];

    return Div("search-bar-container",
        ...items
    )
}