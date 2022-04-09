# 2022-G04
Software Laboratory, 2021/2022, Spring semester

- Tiago Filipe 48265
- Teodosie Pienescu 48267
- Francisco Costa 48282


***

# Phase 1

## Introduction

This document contains the relevant design and implementation aspects of LS project's first phase.
TODO
## Modeling the database

### Conceptual model ###

The following diagram holds the Entity-Relationship model for the information managed by the system.

![ER-Model] (http://TODO)

TODO

We highlight the following aspects:

* (_include a list of relevant design issues_)

TODO

The conceptual model has the following restrictions:

*  The duration is stored in milliseconds and must be bigger than 0
*  The date has the following format: yyyy-mm-dd
*  The distance must be bigger than 0
*  The email must have a valid format example:  "xxxx@email.com"
*  The sports description is optional

### Physical Model ###

The physical model of the database is available in (_link to the SQL script with the schema definition_).

We highlight the following aspects of this model:

* (_include a list of relevant design issues_)


## Software organization

### Open-API Specification ###

https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/docs/sports-api-spec.yaml

In our Open-API specification, we highlight the following aspects:

It´s needed authentication on `POST` and `DELETE` operations this authorization is provided by supplying the bearer token.


### Request Detalis

(_describe how a request goes through the different elements of your solution_)

(_describe the relevant classes/functions used internally in a request_)

(_describe how and where request parameters are validated_)

### Connection Management

(_describe how connections are created, used and disposed_, namely its relation with transaction scopes).

### Data Access

(_describe any created classes to help on data access_).

(_identify any non-trivial used SQL statements_).

### Error Handling/Processing


Our application has a custom error module (_AppError_).
Errors usually thrown by the services layer when handling input from the client.
These errors are later handled on the web-api layer and thrown as HTTP error exceptions.


Errors extend from `AppError` which is our main error class that has a `code` and `message` property.

**AppError** extends then `Exception` class.

```kotlin
/**
* Error class used to represent errors in the application.
* @property code The error code.
* @property message The error message.
  */
  sealed class AppError(val code: Int, message: String): Exception(message)
```
Types of AppError:
  * `InvalidParameter - 2000`
    - is thrown when a parameter that comes from the body of the request or from the URI's parameters is blank (empty string or whitespace characters)
  * `MissingParameter - 2001`
    - is thrown when a required parameter is missing from the body of the request or from the URI's parameters
  * `ResourceNotFound - 2002`
    - is thrown when a parameter that identifies a resource is not found in the repository
  * `UnauthenticatedError - 2003`
    - is thrown when the user is not authenticated
  * `InternalError - 2004`
    - is thrown when an internal error occurs
  
The app errors are handled by an on error filter that runs on every request and converts the errors to HTTP errors.  

```kotlin
/**
 * Object sent in body when an error is thrown in the server
 */
@Serializable
data class HttpError(val code: Int, val message: String?)
```

Each AppError has a corresponding status code:

  * `Bad request - 400`
    - InvalidParameter - 2000
    - MissingParameter - 2001
  * `Not found - 404`
    - ResourceNotFound - 2002
  * `Unauthorized - 401`
    - UnauthenticatedError - 2003
  * `Internal server error - 500`
    - InternalError - 2004
  
  
## Critical Evaluation

(_enumerate the functionality that is not concluded and the identified defects_)

(_identify improvements to be made on the next phase_)







/*

In our Open-API specification, we highlight the following aspects:
On create and delete operations, its needed bearer authorization



mudar por ex o usercreation e response , ter Input e Output


(identify any non-trivial used SQL statements):
query do activity , com o order (query string builder)