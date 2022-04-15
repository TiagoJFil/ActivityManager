
function getHome(mainContent){
    const h1 = document.createElement('h1');
    const text = document.createTextNode("Home")
    h1.append(text);
    mainContent.replaceChildren(h1)
}

function getSports(mainContent){
    const h1 = document.createElement('h1');
    const text = document.createTextNode("Sports")
    h1.append(text);
    mainContent.replaceChildren(h1)
}

function getSport(mainContent, sportsID){
    const h1 = document.createElement('h1');
    const text = document.createTextNode(`Sport ${sportsID}`)
    h1.append(text);
    mainContent.replaceChildren(h1)
}

const handlers = { 
    getHome,
    getSports,
    getSport
}

export default handlers
