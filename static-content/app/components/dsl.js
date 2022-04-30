/**
 * Represents an ul element as a component
 * @param {String} className The class name of the ul element
 * @param {...HTMLElement} children The children of this ul element
 * @returns {HTMLUListElement} an ul element
 */
export function List(className, ...children) {
    return createElement('ul', className, ...children)
}

/**
 * Represents a list item element as a component
 *
 * @param className The class name of the list item element
 * @param  {...HTMLElement} children The children of this element
 * @returns {HTMLLIElement} an li element
 */
export function Item(className, ...children) {
    return createElement('li', className, ...children)
}

/**
 * Creates an anchor element with the given class name and children
 */
export function Anchor(className, href, ...children) {
    const anchor = createElement('a', className, ...children)
    anchor.href = href
    return anchor
}

export function Div(className, ...children) {
    return createElement('div', className, ...children)
}

export function Text(className, text) {
    return createElement('span', className, document.createTextNode(text))
}

export function Icon(...classNames) {
    const i = createElement('i')
    i.classList.add(...classNames)
    return i
}

export function Button(className, onClick, ...children) {
    const button = createElement('button', className, ...children)
    button.onclick = onClick
    return button
}

export function H1(className, text) {
    return createElement('h1', className, document.createTextNode(text))

}

export function Input(className, type, id) {
    const input = createElement('input', className)
    input.type = type
    input.id = id
    return input
}

export function Select(className, id, size , ...children) {
    const select = createElement('select', className, ...children)
    select.size = size
    select.id = id
    return select
}

export function Option(className, value, ...children){
    const option = createElement('option', className, ...children)
    option.value = value
    return option
}

/**
 * Creates an element with the given tag name and children
 *
 * @param {String} tag The tag name of the element to create
 * @param  {...any} children
 * @returns
 */
function createElement(tag, className, ...children) {
    const elem = document.createElement(tag)
    children.forEach(child => {
        elem.append(child)
    })
    if(className) elem.classList.add(className)
    return elem
}

// Domain Specific Language (DSL)

