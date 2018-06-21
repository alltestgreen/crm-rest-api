# crm-rest-api

### Table of Contents
**[Application information](#application-information)**<br>
**[Customer services](#customer-services)**<br>
**[User services](#user-services)**<br>

## Application information

The application uses an in-memory h2 database what can be accessed at: `http://localhost:8080/h2-console` where JDBC URL is `jdbc:h2:mem:cmsdb`,

For testing purposes, the application can populate data on startup by setting the following property in *application.properties*.
``` 
create.default.data=true
``` 
The application is using **OAuth 2.0** authentication mechanism. To invoke any API functionality, you need to request access token by granting user credentials:

For example requesting access token with 'admin':
```
http://localhost:8080/oauth/token?grant_type=password&username=admin&password=admin
``` 

After receiving the token, it is required in the following requests. Expiration is configurable in properties.
```
http://localhost:8080/api/customer/data/1?access_token=95a662ba-a359-4c48-b3cf-d7ef60b0165e
``` 

# Customer services

All endpoint is under **/api/customer/** and requires **USER** role to be accessed.

### 1. List all customers in the database.

```
GET http://localhost:8080/api/customer/list
```

Returns a map of id and full names containing all existing customer:

```
{
    "1": "John Smith",
    "2": "Grace Clayson",
    "3": "Timothy Thompson"
}
```

### 2. Get full customer information, including a photo URI

Expects Path variable with customer id.

```
GET http://localhost:8080/api/customer/details/3
```

Returns `com.abara.model.CustomerDetails` object, includes a resource URL to the customer image.

```
{
    "id": 3,
    "name": "Timothy",
    "surname": "Thompson",
    "imageURI": "http://localhost:8080/api/customer/image/3",
    "createdBy": "admin",
    "modifiedBy": null
}
```

### 3. Create a new customer:

```
POST http://localhost:8080/api/customer/create
```

As Request body, it expects a `com.abara.entity.Customer` object.

```
{
  "name" : "Matthew",
  "surname" : "Mckenzie",
  "image" : {
    "name" : "red-dot.png",
    "type" : "image/png",
    "data" : "iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="
  }
}
```

Upon success, **HTTP 201 CREATED** status returned along with the location header of new resource:

```
location http://localhost:8080/api/customer/details/4
```

### 4. Update an existing customer.

```
PUT http://localhost:8080/api/customer/update
```

As Request body, it expects a `com.abara.entity.Customer` object.

*If Customer Image is not specified in the payload it does not remove existing value. Customer Images can be handled at separate endpoints.*

```
{
  "name" : "Matthew",
  "surname" : "Mckenzie"
}
```

Upon success, **HTTP 200 OK** status returned along with the location header of updated resource:

```
location http://localhost:8080/api/customer/details/4
```

### 5. Delete an existing customer

Expects Path variable with customer id.

```
POST http://localhost:8080/api/customer/delete/3
```

Upon success, **HTTP 200 OK** status returned.

### 6. Upload customer image
Expects Path variable with customer id.
```
POST http://localhost:8080/api/customer/image/upload/2
```
Upon success, **HTTP 201 CREATED** status returned along with the location header of new resource:
```
location http://localhost:8080/api/customer/image/2
```

### 7. Get customer image
Expects Path variable with customer id.
```
GET http://localhost:8080/api/customer/image/2
```
Upon success, **HTTP 200 OK** status returned along with body containing the file content as byte[];

### 8. Delete customer image
Expects Path variable with customer id.
```
POST http://localhost:8080/api/customer/image/delete/2
```
Upon success, **HTTP 200 OK** status returned.

***

When a POST/PUT entity fails validation, a `com.abara.validation.ValidationResult` object returned

```
{
    "entityName": "Customer",
    "errors": [
        "name:size must be between 0 and 256",
        "surname:size must be between 0 and 256"
    ]
}
```

# User services

All endpoint is under **/api/user/** and requires **ADMIN** role to be accessed.

### 1. List users.

```
GET http://localhost:8080/api/user/list
```

Returns a list of `com.abara.model.ApplicationUserDetails` containing all existing user:

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
POST http://localhost:8080/api/user/create
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
location http://localhost:8080/api/user/details/2
```

### 3. Get full user information (excluding password)

Expects Path variable with user id.

```
GET http://localhost:8080/api/user/details/3
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
PUT http://localhost:8080/api/user/update
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
location http://localhost:8080/api/user/details/4
```

### 5. Delete users.

Expects Path variable with user id.

```
POST http://localhost:8080/api/user/delete/3
```

Upon success, **HTTP 200 OK** status returned.

### 6. Change admin status. 

This can be achieved by Update user request specifying the new set of roles.


***

When a POST/PUT entity fails validation, a `com.abara.validation.ValidationResult` object returned

```
{
    "entityName": "User",
    "errors": [
        "username:size must be between 0 and 256",
    ]
}
```