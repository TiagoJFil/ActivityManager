
let USER_INFO = null

function getUserInfo(){
    return USER_INFO
}

function setUserInfo(token){
    USER_INFO = token
}

function isLoggedIn(){
    return !!USER_INFO
}

export  {
    getUserInfo,
    setUserInfo,
    isLoggedIn
}