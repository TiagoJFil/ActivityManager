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
 * @param {String} className The class name of the list item element
 * @param  {...HTMLElement} children The children of this element
 * @returns {HTMLLIElement} an li element
 */
export function Item(className, ...children) {
    return createElement('li', className, ...children)
}

/**
 * Represents a div item element as a component
 *
 * @param {String} className The class name of the list item element
 * @param  {...HTMLElement} children The children of this element
 * @returns {HTMLElement} a div element
 */
export function Div(className, ...children) {
    return createElement('div', className, ...children)
}




/**
 * Represents a text item element as a component
 * 
 * @param {String} className The class name of the list item element
 * @param {String} text The text to display 
 * @returns {HTMLElement} a text element
 */
export function Text(className, text) {
    return createElement('span', className, document.createTextNode(text))
}


/**
 * Represents an icon item element as a component
 * 
 * @param {String} classNames The class names of the list item element
 * @returns {HTMLElement} an icon element
 */
export function Icon(...classNames) {
    const i = createElement('i')
    i.classList.add(...classNames)
    return i
}

/**
 * Represents an button item element as a component
 * 
 * @param {String} className The class name of the list item element
 * @param {Function} onClick a button element
 * @param  {...HTMLElement} children The children of this element
 * @returns {HTMLElement} a button element
 */
export function Button(className, onClick, ...children) {
    const button = createElement('button', className, ...children)
    button.onclick = onClick
    return button
}

/**
 * Represents an h1 item element as a component
 * 
 * @param {String} className The class name of the list item element
 * @param {String} text The text to display
 * @returns {HTMLElement} a h1 element
 */
export function H1(className, text) {
    return createElement('h1', className, document.createTextNode(text))

}

/**
 * Represents an input item element as a component
 *
 * @param {String} className The class name of the list item element
 * @param type The type of the input element
 * @param id The id of the input element
 * @param onInputChange function to be called when the input changes
 * @param placeholder The placeholder of the input element
 * @param startingValue The value of the input element
 * @returns {HTMLElement} an input element
 */
export function Input(className, type, id, onInputChange, placeholder, startingValue, required,min,max) {
    const input = createElement('input', className)
    input.type = type
    input.id = id
    input.value = startingValue ?? ""
    input.required = required ?? false
    input.min = min  
    input.max = max
    input.setAttribute("placeholder", placeholder ?? "")
    input.addEventListener("input", onInputChange)
    return input
}

/**
 * Represents a form element as a component
 *
 * @param className
 * @param children
 * @returns {HTMLElement}
 */
export function Form(className, ...children) {
    const form = createElement('form', className, ...children)
    form.onsubmit = (e) => {
        e.preventDefault()
    }
    form.autocomplete = 'off'
    return form
}

export function Datalist(className,id,...children) {
    const datalist = createElement('datalist',className, ...children)
    datalist.id = id
    return datalist
}

/**
 * Represents a select item element as a component
 * 
 * @param {String} className The class name of the list item element
 * @param size size of the maximum of elements to display
 * @param id The id of the input element
 * @param  {...HTMLElement} children The children of this element 
 * @returns {HTMLElement} an select element 
 */
export function Select(className, id, size , ...children) {
    const select = createElement('select', className, ...children)
    select.size = size
    select.id = id
    return select
}

/**
 * Represents an option item element as a component
 *
 * @param {String} className The class name of the list item element
 * @param {*} value The value of the option element
 * @param label The label of the option element
 * @param  {...HTMLElement} children The children of this element
 * @returns {HTMLElement} an option element
 */
export function Option(className, value, label , ...children){
    const option = createElement('option', className, ...children)
    option.value = value
    option.label = label
    return option
}

/**
 * Creates an anchor element with the given class name and children
 */
export function Anchor(className, href, ...children) {
    const anchor = createElement('a', className, ...children)
    anchor.href = href
    return anchor
}


/**
 * Creates an element with the given tag name and children
 *
 * @param {String} tag The tag name of the element to create
 * @param className The class name of the element to create
 * @param  {...any} children
 * @returns {HTMLElement}  The created element
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

