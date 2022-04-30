import {Text, Button, Div, Icon} from "./dsl.js";
import styles from "../styles.js";

function getActualPage(itemsPerPage){
    // Get skip from the query string in windows location hash
    const skipString = window.location.hash.split("?").find(x => x.includes("skip="))   
    const skip = skipString ? parseInt(skipString.split("=")[1]) : 0
    const actualPage = Math.floor(skip / itemsPerPage);
    return actualPage 
}
export function getPaginationQuery() {
    const limit = getItemsPerPage()
    const pageLinkActive = document.querySelector('.page-link-active')
    const skip = pageLinkActive ? parseInt(pageLinkActive.firstChild.textContent) * limit : 0

    return {
        skip : skip,
        limit : limit
    }
}


export function Pagination(totalElements, onPageChange){
    const MAX_PAGES_PER_VIEW = 7

    const itemsPerPage = getItemsPerPage()
    const actualPage = getActualPage(itemsPerPage)


    const isComplete = totalElements % 10 === 0 && totalElements !== 0
    
    const maxPages = Math.floor(totalElements / itemsPerPage)
    let totalPages = isComplete ? maxPages : maxPages + 1
    const pagesPerView = (totalPages >= MAX_PAGES_PER_VIEW) ? MAX_PAGES_PER_VIEW : totalPages

    const visiblePages = getVisiblePages(totalPages, actualPage, pagesPerView)

    const pageButtons = visiblePages.map((pageNumber) => {
        const skipValue = pageNumber * itemsPerPage
        const className = (actualPage  === pageNumber) ? styles.PAGE_LINK_ACTIVE : styles.PAGE_LINK
        return Button(className, () => { onPageChange(skipValue, itemsPerPage) },
                Text(styles.TEXT, pageNumber+1)
            )
    })

    const startButton = Button(styles.PAGE_ARROW,
        () => onPageChange(0, itemsPerPage),
        Icon(styles.BX_CLASS, 'bx-chevrons-left', styles.PAGINATION_ICONS)
    )
    const previousButton = Button(
        styles.PAGE_ARROW,
        () => onPageChange(actualPage * itemsPerPage - itemsPerPage, itemsPerPage),
        Icon(styles.BX_CLASS, 'bx-chevron-left', styles.PAGINATION_ICONS)
    )

    const endButtonSkip = isComplete ? totalPages * itemsPerPage : totalPages * itemsPerPage - itemsPerPage
    const endButton = Button(styles.PAGE_ARROW, () => onPageChange(endButtonSkip , itemsPerPage),
        Icon(styles.BX_CLASS, 'bx-chevrons-right', styles.PAGINATION_ICONS)
    )
    const nextButton = Button(styles.PAGE_ARROW,
        () => onPageChange(actualPage * itemsPerPage + itemsPerPage, itemsPerPage),
        Icon(styles.BX_CLASS, 'bx-chevron-right', styles.PAGINATION_ICONS)
    )

   
    if(totalPages <= MAX_PAGES_PER_VIEW){
       startButton.disabled = true
       endButton.disabled = true
    }

    if(actualPage === totalPages-1){
        endButton.disabled = true
        nextButton.disabled = true
    }

    if(actualPage === 0){
        startButton.disabled = true
        previousButton.disabled = true
    }

    if(totalPages === 0){
        startButton.disabled = true
        previousButton.disabled = true
        endButton.disabled = true
        nextButton.disabled = true
    }

     const items = [startButton, previousButton, ...pageButtons, nextButton, endButton]

    return Div(styles.PAGINATION, ...items)
}

export function getItemsPerPage(){
    const list = document.querySelector('.list')
    return list ? parseInt(getComputedStyle(list).getPropertyValue('--items-per-page')) : 10
}

function getVisiblePages(totalPages, actualPage, pagesPerView){
    const pages = []
    const sidePages = Math.floor(pagesPerView / 2)


    let start;
    if(actualPage < sidePages){
        start = 0
    }
    else if(actualPage + sidePages >= totalPages){
        start = totalPages - pagesPerView
    }
    else{
        start = actualPage - sidePages 
    }

    for(let i = start; i < start + pagesPerView; i++){
        pages.push(i)
    }


    return pages
}


