# 2022-G04
Software Laboratory, 2021/2022, Spring semester

- Tiago Filipe 48265
- Teodosie Pienescu 48267
- Francisco Costa 48282


***

# Phase 1

## Introduction

This project aims to design and implement the server side of an application that manages physical activities, like running or cycling.

To build this project we used the following technologies:
- **database**: PostgreSQL
- **server side**: http4k library


## Modeling the database

### Conceptual model ###

The following diagram holds the Entity-Relationship model for the information managed by the system.

![ER-Model-Diagram](https://user-images.githubusercontent.com/86708200/162587677-5b465735-4cb8-4593-a00d-69956635c8a0.svg)

We highlight the following aspects:

* An activity has a route that is optional

The conceptual model has the following restrictions:

* Duration time in the format hh:mm:ss.fff
* The date has the following format: yyyy-mm-dd
* The distance must be bigger than 0
* The email must have a valid format example:  "xxxx@email.com"
* The sports description is optional

### Physical Model ###

The physical model of the database is available in https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/sql/createSchema.sql.

![Physical-Model-Diagram](https://user-images.githubusercontent.com/86708200/162586372-2582c695-40ca-41a2-8f1b-9d567c9afd7b.png)

We highlight the following aspects of this model:

It was necessary to create a table for the tokens and emails to normalize the module.


## Software organization

The app is divided in the following layers:
- **Controller layer**: responsible for handling the requests from the client.
- **Services layer**: responsible for the business logic.
- **Data layer**: responsible for the communication with the repository.

Each layer is divided in the following resources:
- **Activity**: responsible for operations related to activities.
- **Sport**: responsible for the operations related to sports.
- **User**: responsible for the operations related to users.
- **Route**: responsible for the operations related to routes.

### App configuration
The app has a configuration module that is responsible for setting up the environment.
Is also responsible for the dependency management of the repositories into the services layer.

#### Database configuration

There are two ways of accessing data

- **PostgreSQL**: Uses a remote postgreSQL database.
    - It has four configurable environment variables:
        - **JDBC_DATABASE_URL**: the url of the database.
        - **JDBC_DATABASE_USER**: the user of the database.
        - **JDBC_DATABASE_PASSWORD**: the password of the database.
        - **JDBC_DATABASE_NAME**: the name of the database.
- **Memory**: Uses an in-memory database. On startup, the database is populated with mock data for testing purposes.

#### Environment

There are two environment types:
- **test**: test environment used for testing the application.
    - Memory.
- **prod**: production environment used for the main application.
    - PostgreSQL.

Another configurable environment variable is **SERVER_PORT** that defines the port the server will listen on

### Open-API Specification ###

https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/docs/sports-api-spec.yaml

Swagger-UI is available at `/api/docs` with the server running.

It´s needed authentication on `POST` and `DELETE` operations this authorization is provided by supplying the bearer token in the `Authorization` header.

Every data transfer object is specified in this specification.

### Request Details and Data Flow

#### Control layer

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
                        "/" bind Method.GET to ::getActivitiesBySport
                    ),
            "/users/{uid}/activities" bind Method.GET to ::getActivitiesByUser
        )
```
Each endpoint has a function responsible for handling requests that are sent to it.
These functions are responsible for parsing the parameters, query, body, headers and sends them to the service layer for validation.

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
- [**SportService**](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/service/SportServices.kt#L1)
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
override fun getRoutes(): List<Route> =
    dataSource.connection.transaction {
        createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM $routeTable")
            rs.toListOf<Route>(ResultSet::toRoute)
        }
    }
```

To avoid attacks by SQL injection, queries that use parameters received from the user input are managed by [PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html) objects.

e.g. `PreparedStatement` being used to check for duplicate email addresses.
```kotlin
// UserDBRepository.kt
/**
 * Checks if any existing user has the given email.
 * @param email the user's email
 * @return [Boolean] true if another user already has the given email or false if it doesn't
 */
override fun hasRepeatedEmail(email: Email): Boolean =
    dataSource.connection.transaction{
        prepareStatement("SELECT * FROM $emailTable WHERE email = ?").use { ps ->
            ps.setString(1, email.value)
            ps.executeQuery().use { resultSet ->
                resultSet.next()
            }
        }
    }
```


#### Database Error Handling
Every database operation is wrapped in a try database operation block.
This block ensures that if any database error occurs a [DatabaseAccessException](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/side/src/main/kotlin/pt/isel/ls/repository/database/utils/transactions.kt#L5) is thrown.

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

### Connection Management

All the database access classes receive the data source from the app configuration. This data source is used to create the connection to the database.

#### Transaction Management

All the database operations are wrapped in a [transaction](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/side/src/main/kotlin/pt/isel/ls/repository/database/utils/transactions.kt#L18) block.
This block ensures that the connection is properly closed and if an error occurs, the transaction is rolled back, which means the database is always in a consistent state.

```kotlin
// transactions.kt
/**
 * Creates a transaction on the given connection properly allocating and disposing connection resources.
 * Resource management for [Statement]s and [ResultSet]s have to be done by the caller in the [block].
 *
 * Commits on success.
 * Rolls back if an exception is thrown propagating it afterwards.
 *
 * @param block the block to execute in the transaction
 * @return the result of the block function invoked in the transaction
 * @throws DataBaseAccessException if an error occurs while accessing the database
 */
inline fun <R> Connection.transaction(block: Connection.() -> R): R
    = tryDataBaseOperation{
        use {
            this.autoCommit = false
            try {
                this.block()
                    .also { commit() }
            } catch (e: Exception) {
                this.rollback()
                throw e
            } finally {
                this.autoCommit = true
            }
        }
    }
```

e.g. Transaction being used on the [delete Activity](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/side/src/main/kotlin/pt/isel/ls/repository/database/ActivityDBRepository.kt#L122) operation:
```kotlin
// ActivityDBRepository.kt   
    /**
     * Deletes the activity identified by the given identifier.
     *
     * @param activityID the id of the activity to delete
     * @return [Boolean] true if it deleted successfully
     */
    override fun deleteActivity(activityID: ActivityID): Boolean {
        dataSource.connection.transaction {
            val query = """DELETE FROM $activityTable WHERE id = ?"""
            prepareStatement(query).use { ps ->
                ps.setInt(1, activityID.toInt())
                return ps.executeUpdate() == 1
            }
        }
    }
```

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
* `2004 - InternalError`
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
* `500 - Internal server error`
    - 2004 - InternalError



## Critical Evaluation

### Defects

Catching DataBaseErrorException on OnErrorFilter creates a dependency between the database and the web-api,
when it should be the service layer to handle this error and throw it as an InternalError. 

### Improvements to be made

 - Add another layer between services and data repositories to manage connection and transaction blocks.
 - Make integration tests using a test database.

