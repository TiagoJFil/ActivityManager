# Phase 3

## Introduction

This project aims to design and implement the server side of an application that manages physical activities, like running or cycling.

To build this project we used the following technologies:
- **database**: PostgreSQL
- **server side**: http4k library
- **client side**: html, css, js


## Modeling the database

### Conceptual model ###

The following diagram holds the Entity-Relationship model for the information managed by the system.

![ER-Model-Diagram](https://user-images.githubusercontent.com/86708200/162587677-5b465735-4cb8-4593-a00d-69956635c8a0.svg)

The conceptual model has the following restrictions:

* Duration time in the format hh:mm:ss.fff
* The date has the following format: yyyy-mm-dd
* The distance is in kilometers
* The email must have a valid format example:  "xxxx@email.com"
* The sports description is optional
* An activity has a route that is optional

### Physical Model ###

The physical model of the database is available [here](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/sql/createSchema.sql).

![Physical-Model-Diagram](https://user-images.githubusercontent.com/86708200/162586372-2582c695-40ca-41a2-8f1b-9d567c9afd7b.png)

We highlight the following aspects of this model:

It was necessary to create a table for the tokens and emails to normalize the model.


## Software organization

### Open-API Specification ###

https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/docs/sports-api-spec.yaml

Swagger-UI is available at `/api/docs` with the server running.

It´s needed authentication on `POST` and `DELETE` operations.
This authorization is provided by supplying the bearer token in the `Authorization` header.

Every data transfer object is specified in this specification.

### App layers

The app is divided in the following layers:
- **API layer**: responsible for handling the requests from the client.
- **Services layer**: responsible for the business logic.
- **Data layer**: responsible for the communication with the repository.

Each layer is divided in the following resources:
- **Activity**: responsible for operations related to activities.
- **Sport**: responsible for operations related to sports.
- **User**: responsible for operations related to users.
- **Route**: responsible for operations related to routes.

### Request Details and Data Flow

#### API layer

Each resource is responsible for handling only the requests that are relevant to it.
Therefore, each resource has a limited set of endpoints that it can handle.

_e.g._ Set of endpoints assigned to the **[Activity](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/api/ActivityRoutes.kt#L118)** resource:
```kotlin
// ActivityRoutes.kt
val handler = routes(
        "/sports/{sid}/activities" bind routes(
            "/" bind Method.POST to ::createActivity,
            "/{aid}" bind Method.DELETE to ::deleteActivity,
            "/{aid}" bind Method.GET to ::getActivity,
            "/" bind Method.GET to ::getActivitiesBySport,
            "/{aid}" bind Method.PUT to ::updateActivity
        ),
        "/users/{uid}/activities" bind Method.GET to ::getActivitiesByUser,
        "/sports/{sid}/users" bind Method.GET to ::getUsersByActivity,
        "/activities" bind routes(
            "/" bind Method.GET to ::getAllActivities,
            "/deletes" bind Method.POST to ::deleteActivities
        )
    )
```
Each endpoint has a function responsible for handling requests that are sent to it.
These functions are responsible for parsing the parameters, query, body and headers sending them to the service layer for validation.

**Resource Controllers**:
- [**ActivityRoutes**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/api/ActivityRoutes.kt#L1)
- [**SportRoutes**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/api/SportRoutes.kt#L1)
- [**UserRoutes**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/api/UserRoutes.kt#L1)
- [**RouteRoutes**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/api/RouteRoutes.kt#L1)

#### Service layer

This layer is responsible for validating the parameters and the data received from the control layer.

If any error occurs meanwhile the validation, the service layer will throw an [exception related](#error-handling-processing) to the context that it occurred.

After validation, the service layer will call the data layer sending the validated parameters to perform the requested operation.

**Resource Services**:
- [**ActivityService**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/service/ActivityServices.kt#L1)
- [**SportService**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/service/SportsServices.kt#L1)
- [**UserService**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/service/UserServices.kt#L1)
- [**RouteService**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/service/RouteServices.kt#L1)

#### Data layer

This layer is responsible for the communication with the database to retrieve and store data.

After each operation, the data layer will return the result to the service layer.

This result can be:
- **Resource id**: the id of the resource that was created.
- **Resource**: the resource that was obtained in the form of entity.
- **Resource list**: the list of resources that were obtained.
- **Boolean**: whether the operation was successful or not.

**Resource repositories**:
- [**ActivityRepository**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/repository/ActivityRepository.kt#L1)
- [**SportRepository**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/repository/SportRepository.kt#L1)
- [**UserRepository**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/repository/UserRepository.kt#L1)
- [**RouteRepository**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/repository/RouteRepository.kt#L1)


The data layer sends entities to the service layer, which in turn converts them to data transfer objects (**DTO**) and sends them to the control layer.

Finally, the control layer returns the result to the client in the form of an **HTTP** response with the respective **status code**.

![Data flow of the application](https://user-images.githubusercontent.com/86708200/162586445-945e83b5-7cfe-44ca-8895-b986b6f61011.svg)

### App configuration
The app has a [configuration module](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/tree/side/src/main/kotlin/pt/isel/ls/config) that is responsible for setting up the environment.

-To change between these modes its possible to use the **APP_ENV_TYPE** environment variable that could either be:
-**PROD**
-**TEST**

#### Database configuration

There are two ways of [accessing data](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/config/db-mode.kt#L17)

- **PostgreSQL**: Uses a remote postgreSQL database.
    - It has a configurable environment variable:
        - **JDBC_DATABASE_URL**: the url of the database.
        - 
- **Memory**: Uses an in-memory database. On startup, the database is populated with mock data for testing purposes.

#### Environment

There are two environment types:
- **test**: test environment used for testing the application.
    - Memory.
- **prod**: production environment used for the main application.
    - PostgreSQL.

Another configurable environment variable is **SERVER_PORT** that defines the port the server will listen on

### Data Access

To access and manage queries to the PostgreSQL database, the data layer uses the [JDBC](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/) library.

This library provides a set of classes which resources need to be properly allocated and disposed.
For that purpose, the data layer uses the kotlin built-in function [kotlin.io.use](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/use.html)
applicable on [Closeable](https://docs.oracle.com/javase/8/docs/api/java/io/Closeable.html) objects.

e.g `kotlin.io.use` being used in a [Statement](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html).
```kotlin
// RouteDBRepository.kt
/**
 * Returns all the routes stored in the repository.
 */
override fun getRoutes(
        paginationInfo: PaginationInfo,
        startLocationSearch: String?,
        endLocationSearch: String?
    ): List<Route> {
        val query = buildSearchQuery(startLocationSearch, endLocationSearch)

        val parameters = listOfNotNull(startLocationSearch, endLocationSearch)
            .map { "${it.trim()}:*".replace(" ", "&") }

        return connection.prepareStatement(query).use { stmt ->
            parameters.forEachIndexed { index, s -> stmt.setString(index + 1, s) }
            stmt.applyPagination(paginationInfo, indexes = Pair(parameters.size + 1, parameters.size + 2))
            val rs = stmt.executeQuery()
            rs.toListOf<Route>(ResultSet::toRoute)
        }
    }
```

To avoid attacks by SQL injection, queries that use parameters received from the user input are managed by [PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html) objects.

_e.g._ `PreparedStatement` being used to check for duplicate email addresses.
```kotlin
// UserDBRepository.kt
/**
 * Checks if any existing user has the given email.
 * @param email the user's email
 * @return [Boolean] true if another user already has the given email or false if it doesn't
 */
override fun hasRepeatedEmail(email: Email): Boolean {
    connection.prepareStatement("SELECT * FROM $emailTable WHERE email = ?").use { ps ->
        ps.setString(1, email.value)
        ps.executeQuery().use { resultSet ->
            return resultSet.next()
        }
    }
}
```


#### Database Error Handling
Every database operation is wrapped in a try database operation block.
This block ensures that if any database error occurs a [DatabaseAccessException](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/utils/repository/transactions.kt#L8) is thrown.

```kotlin
// transactions.kt
/**
 * Tries to execute the given [operation] if it fails, it will throw a [DataBaseAccessException]
 */
inline fun <T> tryDataBaseOperation(operation: () -> T): T{
    return try {
        operation()
    } catch (e: SQLException) {
        throw DataBaseAccessException("Error while accessing the database: ${e.message}")
    }
}
```

#### Non Trivial Database SQL Statements

Regarding the get activities operation, it receives multiple parameters that can be optional and implies to adjust the query accordingly.
Therefore, the query is built using a [query builder](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/side/src/main/kotlin/pt/isel/ls/repository/database/ActivityDBRepository.kt#L105) function.

```kotlin
//ActivityDBRepository.kt
   /**
     * Builds the query to get the activities based on the given parameters.
     * @return the query and if the query has a date parameter
     */
    private fun getActivitiesQueryBuilder(date: LocalDate?, rid: RouteID?, orderBy: Order): Pair<String, Boolean>{
        val sb = StringBuilder()
        sb.append(" SELECT * FROM $activityTable WHERE sport = ? ")
        if (date != null) {
            sb.append("AND date = ? ")
        }
        if (rid != null) {
            sb.append("AND route = ? ")
        }
        sb.append("ORDER BY duration ${if (orderBy == Order.ASCENDING) "ASC" else "DESC"}")
        return Pair(sb.toString(), date != null)
    }
```

For the get sports and get routes operation, the [query](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/repository/database/RouteDBRepository.kt#L44) was built using tsquery to implement a search responsive to the input of the user in the search bar. The sports can be searched by name and description, meanwhile the routes can be searched by start location and end location.


```kotlin
//RouteDBRepository.kt
  /**
     * Auxiliary function to build the query to the get Routes function
     */
    private fun buildSearchQuery(startLocationSearch: String?, endLocationSearch: String?): String {
        return if (startLocationSearch == null && endLocationSearch == null) {
            """SELECT id, startLocation, endLocation, distance, "user" FROM $routeTable LIMIT ? OFFSET ?"""
        } else {
            val startLocationQuery = """SELECT id, startLocation, endLocation, distance, "user" FROM $routeTable """ +
                "WHERE to_tsvector(coalesce(startLocation, '')) @@ to_tsquery(?)"
            val endLocationQuery = """SELECT  id, startLocation, endLocation, distance, "user" FROM $routeTable """ +
                "WHERE to_tsvector(coalesce(endLocation, '')) @@ to_tsquery(?)"
            when {
                startLocationSearch != null && endLocationSearch == null -> """$startLocationQuery LIMIT ? OFFSET ?"""
                startLocationSearch == null && endLocationSearch != null -> """$endLocationQuery LIMIT ? OFFSET ?"""
                else -> """ SELECT * FROM (($startLocationQuery) INTERSECT ($endLocationQuery)) as locationQuery LIMIT ? OFFSET ?"""
            }
        }
    }
```

## Transaction Management

To always maintain a consistent state of the database, the service layer has to have support for transactions.

This feature required the introduction of new domain classes.

- **Transaction**: Represents a transaction that can be used to execute a series of data access operations.

- **TransactionFactory**: Creates a new transaction.

- **TransactionScope**: Provides the data access operations to be executed in a transaction.

### Transaction

```kotlin
/**
 * Represents an app transaction.
 */
sealed interface Transaction {
    
    val scope: TransactionScope
    
    fun begin()
    
    fun commit()
    
    fun rollback()
    
    fun end()
    
}

```
The interface has these four trivial methods that are used to manage the state of the transaction.

#### TransactionFactory

This domain class is responsible for creating a new transaction.

```kotlin

interface TransactionFactory {
    fun getTransaction(): Transaction
}

```

Example of a correct transaction usage:
```kotlin
fun transactionExample(transactionFactory: TransactionFactory) {
    val transaction = transactionFactory.getTransaction()
    transaction.begin()
    try {
        // do something
    } catch (e: Exception) {
        transaction.rollback()
        throw e
    } finally {
        transaction.end()
    }
}

```

Using kotlin higher order functions allowed the creation of an abstraction 
of the transaction management mechanism.


```kotlin
// Transaction.kt

fun <T> execute(block: TransactionScope.() -> T): T {
    begin()
    try {
        val result = this.scope.block()
        commit()
        return result
    } catch (e: Exception) {
        rollback()
        throw e
    } finally {
        end()
    }
}
```
All the database operations are wrapped in an [execute](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/utils/repository/transactions/Transaction.kt#L40) block.

e.g execute being used to create a sport.

```kotlin
// SportService.kt

fun createSport(token: UserToken?, name: String?, description: String?): SportID {
    logger.traceFunction(::createSport.name) { listOf(NAME_PARAM to name, DESCRIPTION_PARAM to description) }

    return transactionFactory.getTransaction().execute {

        val userID = usersRepository.requireAuthenticated(token) // db access
        val safeName = requireParameter(name, NAME_PARAM)
        val handledDescription = description?.ifBlank { null }

        sportsRepository.addSport(safeName, handledDescription, userID) // db access
    }
}

```

#### TransactionScope

This domain class is responsible for providing 
the data access operations to be executed in a transaction.

Is the receiver of the execute function parameter `block`.

Is defined as follows:

```kotlin
sealed class TransactionScope(val transaction: Transaction) {
    abstract val sportsRepository: SportRepository
    abstract val routesRepository: RouteRepository
    abstract val activitiesRepository: ActivityRepository
    abstract val usersRepository: UserRepository
}
```

This implementation restricts the access to the repositories inside the transaction.
Which means the database is always in a consistent state.

As mentioned before, the data access is made through the JDBC library.

One possible implementation of transaction management for JDBC:

```kotlin
class JDBCTransaction(val connection: Connection) : Transaction {

    override val scope = JDBCTransactionScope(this)
    
    override fun begin() {
        connection.autoCommit = false
    }
    
    override fun commit() {
        connection.commit()
    }
    
    override fun rollback() {
        connection.rollback()
    }
    
    override fun end() {
        connection.autoCommit = true
    }
    
    override fun <T> execute(block: TransactionScope.() -> T): T {
        return connection.use { super.execute(block) }
    }
}

```

Each JDBC transaction is associated with a connection, which means that all database operations that are executed in this transaction
have to use the same connection.
This was accomplished by passing the connection to the respective scope and then to the respective repositories, provided by the same scope.
A database operation may not use all the repositories provided by the scope. 
With this in mind all the repository objects are created lazily, so that the scope does not create them
until they are needed.


### Error Handling Processing

Our application has a custom error module [AppError](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/service/errors.kt).

`AppErrors` are thrown by the services layer when validating input from the control layer.
These errors are later handled on the web-api layer and thrown as HTTP error exceptions.

Errors extend from `AppError` which is our main error class that has a `code` and `message` property.

**AppError** extends the `Exception` class.

```kotlin
/**
* Error class used to represent errors in the application.
* @property code The error code.
* @property message The error message.
  */
  sealed class AppError(val code: Int, message: String): Exception(message)
```
Types of AppError:
* `2000 - InvalidParameter`
    - Thrown when a parameter that comes from the body of the request or from the URI's parameters is blank (empty string or whitespace characters)
* `2001 - MissingParameter `
    - Thrown when a required parameter is missing from the body of the request or from the URI's parameters
* `2002 - ResourceNotFound`
    - Thrown when a parameter that identifies a resource is not found in the repository
* `2003 - UnauthenticatedError`
    - Thrown when the user is not authenticated
* `2004 - Forbidden`
    - Thrown when the user tries to access a resource that is not his
* `2005 - InternalError`
    - Thrown when an internal error occurs


The app errors are handled by an [OnErrorFilter](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/api/sports-web-api.kt#L46) that runs on every request and converts the errors to HTTP errors.

```kotlin
/**
 * Object sent in body when an error is thrown in the server
 */
@Serializable
data class HttpError(val code: Int, val message: String?)
```

`AppError` respective HTTP error codes:

* `400 - Bad request`
    - 2000 - InvalidParameter
    - 2001 - MissingParameter
* `404 - Not found`
    - 2002 - ResourceNotFound
* `401 - Unauthorized`
    - 2003 - UnauthenticatedError
* `403 - Forbidden`
    - 2004 - AuthorizationError
* `500 - Internal server error`
    - 2005 - InternalError


## Pagination

For every request that returns a list of resources it supports pagination through the query.

The pagination is defined by two parameters:

- limit - length of the subsequence to return.
- skip - start position of the subsequence to return.

As default, if the query doesn´t have any pagination, the limit is set to 10 and the skip is set to 0.

```
    sports/{sid}/activities?limit=10&skip=0
```

# Web User Interface

The user interface developed uses [Single Page Application (SPA)](https://developer.mozilla.org/en-US/docs/Glossary/SPA) architecture.


## Routing

It was necessary to create our own router to handle the requests to the WebUI.

Routing refers to determining how an application responds to a client request to a particular endpoint.

To define a new route we use the [router.addRouteHandler] function and associate it wih a function that will be called when the route is matched.

_e.g._ Simple route definition(when `home` is requested the function getHome is called):
```js
 router.addRouteHandler('home', handlers.getHome)
```

The route can have placeholders on the path.
To define the path placeholders we use `:param`, where `param` is the name of the parameter.
When the route is matched, the parameters and query are extracted from the request and passed to the function as an object like.
_e.g_: Usage of the router with placeholders and query for the route '/users/:sid'

 ```js
const func = ... a function
router.addRouteHandler('/users/:sid/', func)

const response = router.getRouteHandler('/users/123/')  
// response -> {handler: func, params: {sid: '123'}, query: {} )

const responseWithQuery = router.getRouteHandler('/users/1/?rid=412')
// responseWithQuery -> {handler func , params: {sid: '1'}, query: {rid: '412'} )

```

## Domain Specific Language

A domain-specific language was created in order to facilitate the creation of user interface components.
It uses the following flow of information:

![Information flow of the components](https://user-images.githubusercontent.com/86708200/170840497-8547a02d-450c-43e3-a4fb-0671e0a6170e.svg)

```js
/**
 * Represents a div element as a component
 * @param {String} className The class name of the ul element
 * @param {...HTMLElement} children The children of this ul element
 * @returns {HTMLUListElement} a div element
 */
export function Div(className, ...children) {
    return createElement('div', className, ...children)
}
```

The base components and respective html elements:

- List - `<ul></ul>`
- Item - `<li></li>` used as child of List
- Div - `<div></div>`
- Text - `<span></span>`
- Icon - `<i></i>`
- Button - `<button></button>`
- H1 - `<h1></h1>`
- Input - `<input></input>`
- Select - `<select></select>`
- Option - `<option></option>` used as child of Select
- Image - `<img>` 
- Anchor - `<a></a>`
- Datalist - `<datalist></datalist>`
- Form  - `<form></form>`
- TextArea - `<textarea></textarea>`
- Nav - `<nav></nav>`



These elements were built using the following function:

```js
/**
 * Creates an element with the given tag name and children
 *
 * @param {String} tag The tag name of the element to create
 * @param  {...any} children
 * @returns
 */
function createElement(tag, className, ...children) {
    const elem = document.createElement(tag)
    children.forEach(child => {
        elem.append(child)
    })
    elem.classList.add(className)
    return elem
}
```

The base components can be used in order to build more complex views such as:

```js
/**
 * RouteDetails component
 * Contains the details of a route and its links in form of buttons
 *
 * @param {Route} route route to display
 * @returns {List} the route details component
 */
export default function RouteDetails(route, onEditConfirm) {

    const modal = RouteEdit(route,onEditConfirm)
    
    const onEdit = () => {
        modal.style.display = "flex";
    }

    return List('route',
            Div('route-display',
                Item('distance-item',
                    Text(styles.DETAIL_HEADER, 'Distance: '),
                    Text(styles.TEXT, `${route.distance}`),
                    Text(styles.TEXT, ' Kilometers')
                ),
                Div('locations',
                    Item('start-location-item',
                        Text(styles.TEXT, `${route.startLocation}`)
                    ),
                    Div('locations-line'),
                    Item('end-location-item',
                        Text(styles.TEXT, `${route.endLocation}`)
                    )
                )
            ),
            Div(styles.ICON_GROUP,
                LinkIcon(styles.USER_ICON, `#users/${route.user}`, "Get users details"),
                OuterLinkIcon(styles.MAP_ICON, `https://www.google.com/maps/dir/${route.startLocation}/${route.endLocation}/`),
                ButtonIcon(styles.EDIT_ICON, onEdit, "Edit route")           
            ),
            modal
        )
}

```

## Critical Evaluation

### Defects
Memory data access does not support transactions yet.

### Improvements to be made

- Return the total length of elements in the X-total-count header in all of the endpoints that return a list to make it easier to calculate pagination and avoid
  multiple requests to the api.

- Consider sending the full resource instead of the id only in the foreign keys of the resources to avoid multiple requests on the display functions so the ui loads smoothly.


