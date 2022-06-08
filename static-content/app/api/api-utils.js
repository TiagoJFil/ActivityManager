import {setUserInfo, getUserToken} from "./session.js"


/**
 * Builds the query string from the given query object
 * @param {Object} queryObject - query key-value pairs in json format. e.g: { skip: 0, limit: 10 }
 * @returns {String} - the query as String
 */
export function queryBuilder(queryObject){

    if(queryObject === undefined) return ''
    
    return Object
        .keys(queryObject)
        .map(key => `${key}=${queryObject[key]}`)
        .join('&')
}

/**
 * Returns true if the status code is a success code
 * @param statusCode
 * @returns {boolean}
 */
export function isSuccessful(statusCode) {
    const statusFamily = Math.floor(statusCode / 100)
    return statusFamily === 2
}

/**
 * Returns the response body as JSON if the status code is a success code
 * throws the error returned by the API otherwise
 */
export async function getBodyOrThrow(response) {
    const object = await response.json()

    if (isSuccessful(response.status))
        return object
    else
        throw object
}

/**
 * Makes a simple GET request to the given url with the given query
 * @param {String} uri
 * @param {Object} queryObject query key-value pairs in json format. e.g: { skip: 0, limit: 10 }
 *
 * @returns {Promise} the response body as JSON
 * or throws the error returned by the API if it wasn't successful
 */
export async function getRequest(uri, queryObject) {
    const queryString = (queryObject ? "?" + queryBuilder(queryObject) : '')
    const response = await fetch(uri + queryString)
    return await getBodyOrThrow(response)
}

/**
 * Generic function to fetch a collection of items from the API
 */
export async function fetchResourceList(uri, queryObject, listPropertyName) {
    const listObject = await getRequest(uri, queryObject)
    const queryCopy = { ...queryObject }

    queryCopy.skip = 0
    queryCopy.limit = 10_000_000

    // May fetch more items than the memory limit allows
    // TODO: Should be replaced by sending the total number of items in X-Total-Count response header in the api
    const totalObject = await getRequest(uri, queryCopy)
    listObject.total = totalObject[listPropertyName].length

    return listObject
}



/**
 * Sends a request with the provided method and body
 */
export async function sendRequest(uri, requestMethod, body) {
    return await fetch(uri, {
        method: requestMethod,
        headers: {
            'Authorization': 'Bearer ' + getUserToken(),
            'Content-Type': 'application/json'
        },
        body
    })
}

