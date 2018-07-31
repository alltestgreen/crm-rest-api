# crm-rest-api

### Table of Contents
**[Application information](#application-information)**<br>
**[Authentication](#authentication)**<br>
**[Docker setup](#docker-setup)**<br>
**[Customer services](#customer-services)**<br>
**[User services](#user-services)**<br>

## Application information

The application have multiple profiles, DEV and PROD.

**DEV** profile uses an in-memory h2 database what can be accessed at: `/h2-console` where JDBC URL is `jdbc:h2:mem:cmsdb`
```
mvn clean install spring-boot:run -Dspring.profiles.active=dev -Pdev
```

**PROD** profile uses local MySQL database which datasource URL is defined in the `application-prod.properties`.
```
mvn clean install spring-boot:run -Dspring.profiles.active=prod -Pprod
```
A mysql docker compose provided at: `\src\main\docker\mysql\docker-compose.yml`, you can start it up by changing to its directory and running `docker-compose up`.

*(If your host machine can not access the docker database on localhost, get the specific IP with `docker-machine ip default`)*

For testing purposes, the application can populate data on startup by setting the property `create.default.application.data=true` in `application.properties`.

## Authentication

The application is using **OAuth 2.0** authentication mechanism. To use any API functionality, you need to request access token by granting correct credentials:

For example requesting access token with 'admin':
```
POST http://localhost:8080/oauth/token

Body has x-www-form-urlencoded values:

grant_type=password
username=admin
password=admin

Header has basic auth of the client credentials and content type:

Authorization=Basic Y2xpZW50OnMzY3IzdHA0c3M=
Content-Type=application/x-www-form-urlencoded
```

After receiving the token, it is required in the following requests as a header.
```
http://localhost:8080/api/customers/1

With Header:

Authorization=Bearer 95a662ba-a359-4c48-b3cf-d7ef60b0165e
``` 

## Docker setup

The application supports running on docker by having its own docker image `crm-backend` based on `java:8` image.

The included Maven wrapper can be used to build it locally from the `project directory` the following way on a compiled project:

```
./mvnw docker:build
```

It should create the following files in the target folder:
```
\docker\crm-rest-service-0.0.1-SNAPSHOT.jar
\docker\dockerfile
```

Then the application can be started, by default it needs to expose port 8080.

The command starts the application and maps container port 8080 (second) to port 8080 of the host machine (first).
```
docker run -it -p 8080:8080 crm-backend
```

After successfully started, it can be accessed the following way:
```
GET http://localhost:8080/api/customers
```
*If `localhost` can not be reached from host machine, you need to look up the Host IP of the container:*
```
docker-machine env

-> 

export DOCKER_HOST="tcp://192.168.99.100:2376"
```

And use Host IP address to access the application:
```
GET http://192.168.99.100:8080/api/customers
```

# Customer services

All endpoint is under **/api/customers/** and requires **[USER]** role to be accessed.

### 1. List all customers in the database.

```
GET http://localhost:8080/api/customers
```

Returns a List of `com.abara.model.CustomerDetails` object containing all existing customer:

```
[
    {
        "id": 1,
        "username": "jSmith",
        "name": "John",
        "surname": "Smith",
        "email": "john.smith@company.com",
        "imageURI": null,
        "createdBy": "admin",
        "modifiedBy": null
    }
]
```

### 2. Get full customer information, including a photo URI

Expects Path variable with customer id.

```
GET http://localhost:8080/api/customers/3
```

Returns `com.abara.model.CustomerDetails` object, includes a resource URL to the customer image.

```
{
    "id": 3,
    "username": "timthom",
    "name": "Timothy",
    "surname": "Thompson",
    "email": "timothy.tomphson@company.com",
    "imageURI": "http://localhost:8080/api/customers/image/3",
    "createdBy": "admin",
    "modifiedBy": null
}
```

### 3. Create a new customer:

```
POST http://localhost:8080/api/customers
```

As Request body, it expects a `com.abara.entity.Customer` object.

```
{
  "username" : "ncust",
  "name" : "New",
  "surname" : "Customer",
  "email" : "new.customer@company.com"
}
```

Upon success, **HTTP 201 CREATED** status returned along with the location header of new resource:

```
location http://localhost:8080/api/customers/4
```

### 4. Update an existing customer.

```
PUT http://localhost:8080/api/customers
```

As Request body, it expects a `com.abara.entity.Customer` object.

```
{
  "id" : 2,
  "username" : "updatedUserName",
  "name" : "updatedName",
  "surname" : "updatedCustomer",
  "email" : "updated.customer@company.com"
}
```

Upon success, **HTTP 200 OK** status returned along with the location header of updated resource:

```
location http://localhost:8080/api/customers/4
```

### 5. Delete an existing customer

Expects Path variable with customer id.

```
DELETE http://localhost:8080/api/customers/3
```

Upon success, **HTTP 200 OK** status returned.

### 6. Upload customer image
Expects Path variable with customer id and a MultipartFile param named 'file'.
```
POST http://localhost:8080/api/customers/image/2
```
Upon success, **HTTP 201 CREATED** status returned along with the location header of new resource:
```
location http://localhost:8080/api/customers/image/2
```

### 7. Get customer image
Expects Path variable with customer id.
```
GET http://localhost:8080/api/customers/image/2
```
Upon success, **HTTP 200 OK** status returned along with body containing the file content as byte[];

### 8. Delete customer image
Expects Path variable with customer id.
```
DELETE http://localhost:8080/api/customers/image/delete/2
```
Upon success, **HTTP 200 OK** status returned.

***

When a POST/PUT entity fails validation, `com.abara.validation.ValidationResult` response object returned.

```
{
    "entityName": "Customer",
    "errors": {
        "surname": "size must be between 0 and 256",
        "name": "size must be between 0 and 256"
    }
}
```

# User services

All endpoint is under **/api/users/** and requires **[ADMIN]** role to be accessed.

### 1. List users.

```
GET http://localhost:8080/api/users
```

Returns a list of `com.abara.model.ApplicationUserDetails` containing all existing user (excluding password):

```
[
    {
        "id": 1,
        "username": "admin",
        "roles": [
            {
                "name": "USER"
            },
            {
                "name": "ADMIN"
            }
        ]
    },
    {
        "id": 2,
        "username": "user",
        "roles": [
            {
                "name": "USER"
            }
        ]
    }
]
```

### 2. Create users.

```
POST http://localhost:8080/api/users
```

As Request body, it expects a `com.abara.entity.User` object.

```
{
  "username" : "Cassie",
  "password" : "P@ssw0rd",
  "roles" : [ {
    "name" : "USER"
  }, {
    "name" : "ADMIN"
  } ]
}
```

Upon success, **HTTP 201 CREATED** status returned along with the location header of new resource:

```
location http://localhost:8080/api/users/2
```

### 3. Get full user information (excluding password)

Expects Path variable with user id.

```
GET http://localhost:8080/api/users/3
```

Returns `com.abara.model.ApplicationUserDetails` object.

```
{
    "id": 2,
    "username": "user",
    "roles": [
        {
            "name": "USER"
        }
    ]
}
```

### 4. Update users.

```
PUT http://localhost:8080/api/users
```

As Request body, it expects a `com.abara.entity.User` object.

*If Password is not specified in the payload it does not changing existing value.*

```
{
  "username" : "Cassie",
  "roles" : [ {
    "name" : "USER"
  }
}
```

Upon success, **HTTP 200 OK** status returned along with the location header of updated resource:

```
location http://localhost:8080/api/users/4
```

### 5. Delete users.

Expects Path variable with user id.

```
DELETE http://localhost:8080/api/users/3
```

Upon success, **HTTP 200 OK** status returned.

### 6. Change admin status. 

This can be achieved by [Update user request](#4.-update-users.) specifying the new set of roles in the request bodyó.

***

When a POST/PUT entity fails validation, `com.abara.validation.ValidationResult` response object returned.

```
{
    "entityName": "User",
    "errors": {
        "username": "size must be between 0 and 256"
    }
}
```