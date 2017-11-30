# stubyaml [![CircleCI](https://circleci.com/gh/int128/stubyaml.svg?style=shield)](https://circleci.com/gh/int128/stubyaml) [![Gradle Status](https://gradleupdate.appspot.com/int128/stubyaml/status.svg)](https://gradleupdate.appspot.com/int128/stubyaml/status)

A YAML based HTTP stub server for API testing.

It has following key features:

- Easy to run and deploy
- Declarative API definition with YAML
- Flexible template and pattern matching with Groovy
- Continuous reload on file change


## Getting Started

Java 8 is required.
Download the latest JAR from [here](https://github.com/int128/stubyaml/releases).

```
mkdir -p data
vim data/users.get.yaml
```

Create YAML file `data/users.get.yaml` as following:

```yaml
- response:
    headers:
      content-type: application/json
    body:
      - id: 1
        name: Foo
      - id: 2
        name: Bar
```

Run the application:

```
java -jar stubyaml.jar
```

Call the API:

```
curl -v http://localhost:8080/users
> GET /users HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.47.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Date: Thu, 16 Nov 2017 06:50:13 GMT
< Content-Type: application/json
< Transfer-Encoding: chunked
<
[{"name":"Foo","id":1},{"name":"Bar","id":2}]
```

The stub will reload YAML files if anyone has been changed or new one has been created.


## Options

### Logging

You can write log to a file.

By following option, the stub writes log to `logs/spring.log`, rotates when it reaches 10MB and keeps up to 8 files.
See [Spring Boot features: Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html) for more.

```
# Command line option
java -jar stubyaml.jar --logging.path=logs

# Environment variable
export LOGGING_PATH=logs
java -jar stubyaml.jar
```

### Request and response logging

You can turn on request and response log as follows:

```
# Command line option
java -jar stubyaml.jar --request-response-log

# Environment variable
export REQUEST_RESPONSE_LOG=1
java -jar stubyaml.jar
```

The stub will write request and response log as follows:

```
2017-11-16 06:52:36.289  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> GET /users
2017-11-16 06:52:36.291  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> User-Agent: curl/7.47.0
2017-11-16 06:52:36.291  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> Accept: */*
2017-11-16 06:52:36.291  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> Host: localhost:8080
2017-11-16 06:52:36.291  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|>
2017-11-16 06:52:36.414  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< 200 OK
2017-11-16 06:52:36.415  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< Date: Thu, 16 Nov 2017 06:52:36 GMT
2017-11-16 06:52:36.415  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< x-uuid: 1f7d7e56-f669-474c-83ca-7003352b67f6
2017-11-16 06:52:36.415  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< Content-Type: application/json
2017-11-16 06:52:36.415  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<
2017-11-16 06:52:36.419  INFO 19613 --- [qtp766696861-16] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< [{"name":"Foo","id":1},{"name":"Bar","id":2}]
```


## Examples

### Handling various HTTP methods

Specify HTTP method in the extension part of filename.
For example, create `data/users.post.yaml` for handling POST method.
Following methods are supported.

- GET
- HEAD
- POST
- PUT
- PATCH
- DELETE
- OPTIONS
- TRACE


### Serving various contents

Set the content type header as follows:

```yaml
- response:
    headers:
      content-type: application/xml
    body: |
      <?xml version="1.0" encoding="UTF-8"?>
      <users>
        <user>
          <id>1</id>
          <name>Foo</name>
        </user>
      </users>
```

Set the character set if needed:

```yaml
- response:
    headers:
      content-type: text/plain;charset=Shift_JIS
    body: Example
```


### Path variables

A braced string in the file path is treated as a path variable.
For example, create `/users/{userId}.get.yaml` for handling `/users/1`, `/users/2` and so on.


### Using template

Response header value and body are parsed as a Groovy template.
Following variables are available in a script block `${}`.

Variable    | Object
------------|-------
`path`      | Path variables
`headers`   | Request headers
`params`    | Query parameters
`body`      | Request body

Type of the request body may be one of following:

Content type of request | Type of request body
------------------------|---------------------
`application/x-www-form-urlencoded` | `Map<String, String>`
`multipart/form-data` | `Map<String, Part>`
`application/json` and subset | `Map<String, Object>`
`application/xml` and subset | `Map<String, Object>`
`text/*` | `String`
Otherwise | `null`

For example, create `/users/{userId}.get.yaml` as following:

```yaml
- response:
    headers:
      content-type: application/json
    body:
      id: ${path.userId},
      name: User${path.userId}
```

The stub will return the following response on the request `GET /users/100`:

```json
{
  "id": 100,
  "name": "User100"
}
```


### Pattern matching

A YAML file has one or more rules.
The stub evaluates all rules and returns the first matched `response`.

Here is the example of `/numbers.get.yaml` as follows:

```yaml
- when: params.order == 'desc'
  response:
    headers:
      content-type: application/json
    body: [3, 2, 1]

- when: params.order == 'asc'
  response:
    headers:
      content-type: application/json
    body: [1, 2, 3]
```

The stub will return the following response on the request `GET /numbers?order=asc`:

```json
[1, 2, 3]
```

And on the request `GET /numbers?order=desc`:

```json
[3, 2, 1]
```

If the last resort is not defined, the stub will return 404.


### Data driven testing

Let's see the example.

Request condition: `path.userId` | Response variable: `tables.userName` | Response variable: `tables.age`
---------------------------------|--------------------------------------|--------------------------------
1 | Foo | 35
2 | Bar | 100
3 | Baz | 3

Create `/users/{userId}.get.yaml` with following rule.

```yaml
- response:
    headers:
      content-type: application/json
    body:
      id: ${path.userId}
      name: ${tables.userName}
      age: ${tables.age}
    tables:
    - name: userName
      key: path.userId
      values:
        1: Foo
        2: Bar
        3: Baz
    - name: age
      key: path.userId
      values:
        1: 35
        2: 100
        3: 3
```

The stub will return the following response on the request `GET /users/1`:

```json
{
  "id": 1,
  "name": "Foo",
  "age": 35
}
```

And on the request `GET /users/2`:

```json
{
  "id": 2,
  "name": "Bar",
  "age": 100
}
```


## Delayed response

Use `delay` attribute in milliseconds to simulate network latency.

For example, create `/users.post.yaml` as following:

```yaml
- response:
    delay: 500
    headers:
      content-type: application/json
    body:
      id: 1
```

Send the request `POST /users` and the stub will return a response after 500 ms.
