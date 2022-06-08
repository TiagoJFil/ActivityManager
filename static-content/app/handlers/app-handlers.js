import {Div, H1, Text, Image, Button, Anchor} from "../components/dsl.js"
import { sportHandlers } from "./sports-handlers.js";
import { userHandlers } from "./users-handlers.js";
import { routeHandlers } from "./routes-handlers.js";
import { activityHandlers } from "./activities-handlers.js";
import { getBodyOrThrow, queryBuilder } from "../api/api-utils.js";
import { ErrorToast, SuccessToast , InfoToast} from "../toasts.js";
import CreateUser from "../components/creates/CreateUser.js";
import styles from "../styles.js";
import Login from "../components/Login.js";
import { userApi } from "../api/api.js";
import {setUserInfo ,isLoggedIn, logOut} from "../api/session.js"

import { reloadNav} from "./utils.js"

/**
 * Displays the home page 
 */
function getHome() {

    return [
        Div("home-page",
            H1(styles.HEADER, 'Sports Isel'),
            Div("image-group", 
                Image("home-image", "homeImage", "./img/running.svg","https://api.vexels.com/v1/download/263640/"),
                Image("home-image", "homeImageGirl", "./img/running-girl.svg","https://api.vexels.com/v1/download/263646/")
            )
        )
    ]

}

const NOT_FOUND_MESSAGE = 'Sorry, the page you are looking for does not exist. Try heading to the home page.'

/**
 * Displays a page to indicate that nothing was found
 */
function getNotFoundPage() {

    return[
        H1(styles.HEADER,'404 - Not Found'),
        Text(styles.TEXT, NOT_FOUND_MESSAGE),
        Div(styles.SPACER)
    ]
        

}

/**
 * Displays the error page with the given error message
 */
function getErrorPage( error) {
    let message;
    let header;
    
    switch(error.code){
        case 2002:
            header = 'Content Not Found'
            message = NOT_FOUND_MESSAGE
            break
        default:
            header = 'Error'
            message = 'An error has occurred. Please try again later.'
            break
    }

    return[
        H1(styles.HEADER, header),
        Text(styles.TEXT, message),
        Div(styles.SPACER)
    ]
}





function getLogin(){

    const onLoginConfirm = async (email, password, Button) => {
        try{
            Button.disabled = true
            const auth = await userApi.login(email, password)
            const user = userApi.fetchUser(auth.id)
            
            user.then( userObj => {
                SuccessToast(`Welcome ${userObj.name}`).showToast()
                reloadNav()
            })
            
            setUserInfo(auth)
            window.location.hash = "home"
        }
        catch(e) {
            ErrorToast(e.message).showToast()
            Button.disabled = false
            
            return false
        }
    }

    return[
        Div("login-page",
            H1(styles.HEADER, 'Sign In'),
            Div(styles.LOGIN_ELEMS,
                Login(onLoginConfirm),
                Div(styles.SPACER),
                Text(styles.TEXT, "Don't have an account yet? "),
                    Anchor(styles.REGISTER_ANCHOR, "#register", Text(styles.TEXT, "Register"))
            )
        )
    ]
}

function getLogout(){
    if(!isLoggedIn()){
        window.location.hash = "home"
        return
    }
    logOut()
    InfoToast(`Goodbye :(`).showToast()


    reloadNav()
    window.location.hash = "home"
}

function getRegister(){

    const onRegisterConfirm = async (name, email, password, reinsertedPassword, Button) => {
        try{ 
            if(password != reinsertedPassword){
                ErrorToast("Passwords do not match").showToast()
                return 
            }

            Button.disabled = true

            const User = await userApi.createUser(name, email, password)
            window.location.hash = "home"
            
            setUserInfo(User)
            reloadNav()
            SuccessToast(`Welcome ${name}`).showToast()
            
            
        }catch(e){
            let message = ''
            if(e.code = 2000)
                message = "Name should not be empty or email already taken"
            else
                message = "An error has occurred. Please try again later."
                
            ErrorToast(message).showToast()
            Button.disabled = false
            return
        }
    }

    return[
        H1(styles.HEADER, 'Register'),
        Div(styles.REGISTER_ELEMS,
            CreateUser(onRegisterConfirm)
        )
    ]
}

/**
 * The function to be called when the pagination is changed
 * 
 * @param {string} path  the path of the page to be loaded
 * @param {Text} currentQuery the current query
 * @param {Number} skip  the skip to be used in the query
 * @param {Number} limit   the limit to be used in the query
 */
export function onPaginationChange(path, currentQuery, skip, limit){

    const query = currentQuery ? {...currentQuery} : {}

    query.skip = skip
    query.limit = limit
    
    window.location.hash = `#${path}?${queryBuilder(query)}`
}

export default {
    getHome,
    getNotFoundPage,
    getErrorPage,
    getLogin,
    getRegister,
    getLogout,
    getSports: sportHandlers.displaySportList,
    getSport: sportHandlers.displaySportDetails,
    getUsers: userHandlers.displayUserList,
    getUser: userHandlers.displayUserDetails,
    getRoutes: routeHandlers.displayRouteList,
    getRoute: routeHandlers.displayRouteDetails,
    createActivity: activityHandlers.createActivity,
    getActivities: activityHandlers.displayActivityList,
    getActivity: activityHandlers.displayActivityDetails,
    getUsersByActivity: userHandlers.displayUsersByActivity,
    getActivitiesByUser: activityHandlers.displayActivitiesByUser,
    getActivitiesSearch: activityHandlers.displaySearchActivities,
    getUsersByRanking: userHandlers.displayUsersByRanking,
    createSport: sportHandlers.createSport,
    createRoute: routeHandlers.createRoute
}


