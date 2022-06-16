
const ID_PROP = "id"
const TOKEN_PROP = "authToken"

function getUserInfo(){
    return {
        ID_KEY: sessionStorage.getItem(ID_PROP),
        TOKEN_PROP : sessionStorage.getItem(TOKEN_PROP)
    }
}

function setUserInfo(info){
    sessionStorage.setItem(TOKEN_PROP, info[TOKEN_PROP])
    sessionStorage.setItem(ID_PROP, info[ID_PROP])
}

function getUserToken(){
    return sessionStorage.getItem(TOKEN_PROP)
}

function logOut(){
    sessionStorage.removeItem(ID_PROP)
    sessionStorage.removeItem(TOKEN_PROP)
}

function isLoggedIn(){
    return !!sessionStorage.getItem(TOKEN_PROP)
}

export  {
    getUserInfo,
    setUserInfo,
    isLoggedIn,
    getUserToken,
    logOut
}