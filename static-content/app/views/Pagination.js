import {Anchor, Div, Text} from "./utils.js";

function getActualPage(itemsPerPage){
    const path = window.location.hash
    const page = path.split('=')[1]
    if(!page){
        return 0
    }
    return page.split('&')[0] / itemsPerPage
}


export function Pagination(view, totalElements){
    const itemsPerPage = getItemsPerPage()
    let anchorList = []

    const LIMIT = totalElements / itemsPerPage

    for (let i = 0; i < LIMIT; ++i){
        const skipValue = i * itemsPerPage
        const anchorClassName = i === getActualPage(itemsPerPage) ? 'page-link-active' : 'page-link'
        const anchor =
            Anchor(anchorClassName,`#${view}?skip=${skipValue}&limit=${itemsPerPage}`,
                Text('page-text',i)
            )

        anchorList.push(anchor)
    }
    return Div( 'pagination',
        ...anchorList
    )
}

export function getItemsPerPage(){
    const list = document.querySelector('.list')
    return list ? parseInt(getComputedStyle(list).getPropertyValue('--items-per-page')) : 10
}


