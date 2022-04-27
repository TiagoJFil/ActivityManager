
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

/**
 * Creates an anchor element with the given class name and children
 */
export function Anchor(className, href, ...children){ 
    const anchor = createElement('a', className, ...children)
    anchor.href = href
    return anchor
}

export function Div(className, ...children){
    return createElement('div', className, ...children)
}

export function Text(className, text){
    return createElement('span', className, document.createTextNode(text))
}

export function Icon(...classNames){
    const i = createElement('i')
    i.classList.add(...classNames)
    return i
}

export function H1(className, text){
    return createElement('h1', className , document.createTextNode(text))

}

//Todo tirar os links daqui
export function ActivitiesLink(text,sid){
    return Anchor('link', `#sports/${sid}/activities`,
        Text('text', text)
    )
}

export function UserLink(ownershipText,linkText,uid){
    return Div('userlink',
        Text('user-text', ownershipText),
        Anchor('link',`#users/${uid}`,
            Text('text',
            linkText
            )
        ),
        Icon('bx','bx-user')
    )
    
}



const ITEMS_PER_PAGE = 10

function getActualPage(){
    const path = window.location.hash
    const page = path.split('=')[1]
    if(!page){
        return 0
    }
    return page.split('&')[0] / ITEMS_PER_PAGE
}
export function Pagination(view,totalElements){
    let anchorList = []

    const LIMIT = totalElements / ITEMS_PER_PAGE

    for (let i = 0; i < LIMIT; ++i){
        const skipValue = i * ITEMS_PER_PAGE
        const anchorClassName = i === getActualPage() ? 'page-link-active' : 'page-link'
        const anchor =
            Anchor(anchorClassName,`#${view}?skip=${skipValue}&limit=${ITEMS_PER_PAGE}`,
                Text(i,i)
            )

        anchorList.push(anchor)
    }
    return Div( 'pagination',
        ...anchorList
    )
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

