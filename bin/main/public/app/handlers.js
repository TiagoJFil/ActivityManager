


const API_BASE_URL = 'http://localhost:9000/api/'
const SPORT_URL = API_BASE_URL + 'sports'

function getHome(mainContent){
    const text = document.createTextNode("Home")
    const h1 = createElement('h1', text)
    mainContent.replaceChildren(h1)
}

async function getSports(mainContent){
    const response = await fetch(SPORT_URL)
    console.log(response)
    const sportsObject = await response.json()
    const sportList = sportsObject.sports
    console.log(sportList)
    const li_array = sportList.map(sport => {
        const text = document.createTextNode(`${sport.id} - ${sport.name}`)
        return createElement('li', text)
    })
    const ul = createElement('ul', ...li_array)
    mainContent.replaceChildren(ul)
}



/*
This example creates the students views using directly the DOM Api
But you can create the views in a different way, for example, for the student details you can:
    createElement("ul",
        createElement("li", "Name : " + student.name),
        createElement("li", "Number : " + student.number)
    )
or
    ul(
        li("Name : " + student.name),
        li("Number : " + student.name)
    )
Note: You have to use the DOM Api, but not directly
*/

function createElement(tag, ...children){
    const element = document.createElement(tag)
    children.forEach(child => {
        element.appendChild(child)
    })
    return element
}

const handlers = { 
    getHome,
    getSports
}
export default handlers