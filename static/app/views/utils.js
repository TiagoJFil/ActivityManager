
/**
 * Represents an ul element as a component
 * @param {String} className The class name of the ul element
 * @param {...HTMLElement} children The children of this ul element
 * @returns {HTMLUListElement} an ul element
 */
export function List(className, ...children){
    return createElement('ul', className, ...children)
}

/**
 * Represents a list item element as a component
 *
 * @param className The class name of the list item element
 * @param  {...HTMLElement} children The children of this element
 * @returns {HTMLLIElement} an li element
 */
export function Item(className, ...children){ 
    return createElement('li', className, ...children)
}

export function Anchor(className, ...children){ 
    return createElement('a', className, ...children)
}

/**
 * @receiver {String} The text to display on the anchor
 * @param {String} href path this anchor should link to
 * @returns an Anchor element that links to the given href
 */
String.prototype.linkTo = function(href){
    const anchor = Anchor("link", document.createTextNode(this))
    anchor.href = href
    return anchor
}

/**
 * Creates an element with the given tag name and children
 * 
 * @param {String} tag The tag name of the element to create
 * @param  {...any} children 
 * @returns 
 */
function createElement(tag, className, ...children){
    const elem = document.createElement(tag)
    children.forEach( child => {
        elem.append(child)
    })
    elem.classList.add(className)
    return elem
}

