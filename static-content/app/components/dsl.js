// Domain Specific Language (DSL)

/**
 * Represents an ul element as a component
 * @param {String} className The class name of the ul element
 * @param {...HTMLElement} children The children of this ul element
 * @returns {HTMLUListElement} an ul element
 */
export function List(className, ...children) {
    return createElement('ul', className, null, ...children)
}

/**
 * Represents a list item element as a component
 *
 * @param {String} className The class name of the list item element
 * @param  {...HTMLElement} children The children of this element
 * @returns {HTMLLIElement} an li element
 */
export function Item(className, ...children) {
    return createElement('li', className, null, ...children)
}

/**
 * Represents a div item element as a component
 *
 * @param {String?} className The class name of the list item element
 * @param  {...HTMLElement?} children The children of this element
 * @returns {HTMLElement} a div element
 */
export function Div(className, ...children) {
    return createElement('div', className, null, ...children)
}


/**
 * Represents a text item element as a component
 *
 * @param {String} className The class name of the list item element
 * @param {String} text The text to display
 * @returns {HTMLElement} a text element
 */
export function Text(className, text) {
    return createElement('span', className, null, document.createTextNode(text))
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
    const button = createElement('button', className, null, ...children)
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
    return createElement('h1', className, null, document.createTextNode(text))
}

/**
 * Represents a Nav element as a component
 *
 * @param {String} className The class name of the nav element
 * @param id The id of the nav element
 * @param  {...any} children  The children of this element
 * @returns {HTMLElement} a nav element
 */
export function Nav(className, id, ...children) {
    return createElement('nav', className, id, ...children)
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
 * @param required If the input is required to fill
 * @param min If the input is a number specifies the minimum value
 * @param max If the input is a number specifies the maximum value
 * @returns {HTMLElement} an input element
 */
export function Input(className, type, id, onInputChange, placeholder, startingValue, required, min, max) {
    const input = createElement('input', className, id)
    input.type = type
    input.value = startingValue ?? ""
    input.required = required ?? false
    if(min) input.min = min
    if(max) input.max = max
    input.setAttribute("placeholder", placeholder ?? "")
    input.addEventListener("input", onInputChange)
    return input
}

/**
 * Represents a TextArea element as a component
 *
 * @param {String} className The class name of the text area element
 * @param {String} id The id of the text area element
 * @param {Function} onInputChange function to be called when the input changes
 * @param {String} placeholder The placeholder of the text area element
 * @param {String} startingValue The value of the text area element
 * @param {Boolean} required Whether the text area is required
 * @returns {HTMLElement} a text area element
 */
export function TextArea(className, id, onInputChange, placeholder, startingValue, required){
    const element = createElement('textarea', className, id)
    element.setAttribute("placeholder", placeholder ?? "")
    element.textContent = startingValue ?? ""
    element.required = required ?? false
    element.setAttribute("wrap", "soft")
    return element
}

/**
 * Represents a form element as a component
 *
 * @param className
 * @param children
 * @returns {HTMLElement}
 */
export function Form(className, ...children) {
    const form = createElement('form', className, null, ...children)
    form.onsubmit = (e) => {
        e.preventDefault()
    }
    form.autocomplete = 'off'
    return form
}

/**
 * Represents a datalist element as a component
 *
 * @param {String} className The class name of the datalist element
 * @param {String} id The id of the datalist element
 * @param {...HTMLElement} children The children of this element
 * @returns {HTMLElement} a datalist element
 */
export function Datalist(className,id,...children) {
    return createElement('datalist', className, id, ...children)
}

/**
 * Represents a select item element as a component
 *
 * @param {String} className The class name of the list item element
 * @param size size of the maximum of elements to display
 * @param id The id of the input element
 * @param {...HTMLElement} children The children of this element
 * @returns {HTMLElement} an select element
 */
export function Select(className, id, size , ...children) {
    const select = createElement('select', className, id, ...children)
    select.size = size
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
    const option = createElement('option', className, null, ...children)
    option.value = value
    option.label = label
    return option
}

/**
 * Creates an anchor element with the given class name and children
 */
export function Anchor(className, href, ...children) {
    const anchor = createElement('a', className, null, ...children)
    anchor.href = href
    return anchor
}

/**
 * Creates an image element with the given class name and children
 *
 * @param {String} className The class name of the image element
 * @param {String} id The id of the image element
 * @param {String} src The source of the image element
 * @param {String} alt The alt of the image element
 */
export function Image(className, id, src, alt){
    const image = createElement("img", className, id)
    image.src = src
    image.alt = alt
    return image
}


/**
 * Creates a hidden element with no data
 * @returns {HTMLElement} a input element
 */
export function HiddenElem() {
    const input = document.createElement('input')
    input.type = 'hidden'
    input.value = 'null'
    return input
}

/**
 * Creates an element with the given tag name and children
 *
 * @param {String} tag The tag name of the element to create
 * @param className The class name of the element to create
 * @param id The id of the element to create
 * @param  {...any} children
 * @returns {HTMLElement}  The created element
 */
function createElement(tag, className, id, ...children) {
    const elem = document.createElement(tag)
    children.forEach(child => {
        elem.append(child)
    })
    if (id !== undefined && id != null) elem.id = id
    if (className) elem.classList.add(className)
    return elem
}

