import { Text, Button, Div } from "./utils.js";
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

    let totalPages = Math.max(Math.floor(totalElements / itemsPerPage), 1)

    const actualPage = getActualPage(itemsPerPage)
    
    const pagesPerView = (totalPages >= MAX_PAGES_PER_VIEW) ? MAX_PAGES_PER_VIEW : totalPages

    const visiblePages = getVisiblePages(totalPages, actualPage, pagesPerView)


    const pageButtons = visiblePages.map((pageNumber) => {
        const skipValue = pageNumber * itemsPerPage
        const className = (actualPage  === pageNumber) ? styles.PAGE_LINK_ACTIVE : styles.PAGE_LINK
        return Button(className, () => { onPageChange(skipValue, itemsPerPage) },
                Text(styles.TEXT, pageNumber+1)
            )
    }) 
    
    const startButton = Button(styles.PAGE_LINK, () => onPageChange(0, itemsPerPage),
        Text(styles.TEXT, '<<<')
    )

    const endButtonSkip = totalPages === 1 ? 0 : totalPages * itemsPerPage

    const endButton = Button(styles.PAGE_LINK, () => onPageChange(endButtonSkip , itemsPerPage),
        Text(styles.TEXT, '>>>')
    )

    const items = [startButton, ...pageButtons, endButton]//.map(x => Item('page-item', x))
    
    return Div(styles.PAGINATION, ...items)
}

export function getItemsPerPage(){
    const list = document.querySelector('.list')
    return list ? parseInt(getComputedStyle(list).getPropertyValue('--items-per-page')) : 10
}

// 20 pages
// 5 pages per side
// actual page = 1
// visible pages = [1, 2, 3, 4, 5]

// 20 pages
// 5 pages per side
// actual page = 2
// visible pages = [1, 2, 3, 4, 5]

// 20 pages
// 5 pages per side
// actual page = 3
// visible pages = [1, 2, 3, 4, 5]
               
// 20 pages
// 5 pages per side
// actual page = 4
// visible pages = [2, 3, 4, 5, 6]

// 20 pages
// 5 pages per side
// actual page = 5
// visible pages = [16, 17, 18, 19, 20]

function getVisiblePages(totalPages, actualPage, pagesPerView){
    const pages = []
    const sidePages = Math.floor(pagesPerView / 2)

    let start;
    if(actualPage < sidePages){
        start = 0
    }
    else if(actualPage + sidePages >= totalPages){
        start = totalPages - (pagesPerView - 1)
    }
    else{
        start = actualPage - sidePages 
    }

    for(let i = start; i < start + pagesPerView; i++){
        pages.push(i)
    }
    
    return pages
}


