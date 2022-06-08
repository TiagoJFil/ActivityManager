
let USER_INFO = null

function getUserInfo(){
    return USER_INFO
}

function setUserInfo(info){
    USER_INFO = info
}

function getUserToken(){
    return USER_INFO ? USER_INFO["authToken"] : null
}

function isLoggedIn(){
    return !!USER_INFO
}

export  {
    getUserInfo,
    setUserInfo,
    isLoggedIn,
    getUserToken
}