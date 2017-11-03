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
    body: |
      [
        {
          "id": 1,
          "name": "Foo"
        },
        {
          "id": 2,
          "name": "Bar"
        }
      ]
```

Run the application:

```
java -jar stubyaml.jar
```

Invoke the API:

```
curl -v http://localhost:8080/users
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /users HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Date: Wed, 01 Nov 2017 00:56:43 GMT
< Content-Type: application/json
< Content-Length: 83
<
[
  {
    "id": 1,
    "name": "Foo"
  },
  {
    "id": 2,
    "name": "Bar"
  }
]
* Connection #0 to host localhost left intact
```

The stub rescan YAML files if anyone has been changed or new one has been created.


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
2017-11-03 13:46:37.974  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> GET /users
2017-11-03 13:46:37.977  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> User-Agent: curl/7.54.0
2017-11-03 13:46:37.977  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> Accept: */*
2017-11-03 13:46:37.977  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|> Host: 127.0.0.1:8080
2017-11-03 13:46:37.977  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|>
2017-11-03 13:46:38.042  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< 200 OK
2017-11-03 13:46:38.043  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< Content-Length: 83
2017-11-03 13:46:38.044  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< Date: Fri, 03 Nov 2017 04:46:37 GMT
2017-11-03 13:46:38.044  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< Content-Type: application/json
2017-11-03 13:46:38.044  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<
2017-11-03 13:46:38.046  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< [
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<   {
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<     "id": 1,
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<     "name": "Foo"
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<   },
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<   {
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<     "id": 2,
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<     "name": "Bar"
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|<   }
2017-11-03 13:46:38.047  INFO 17656 --- [tp1555895711-31] o.h.s.RequestAndResponseLoggingFilter    : 127.0.0.1|< ]
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


### Path variables

A braced string in the file path is treated as a path variable.
For example, create `/users/{userId}.get.yaml` for handling `/users/1`, `/users/2` and so on.


### Groovy template

Header value and body are parsed as a Groovy template.
You can access to following objects via the script block `${}`.

Prefix      | Object
------------|-------
`request.`  | `HttpServletRequest` object bound to current request
`path.`     | Path variables
`headers.`  | Request headers
`params.`   | Request parameters (query string or form)
`body.`     | Request body

For example, create `/users/{userId}.get.yaml` as following:

```yaml
- response:
    headers:
      content-type: application/json
    body: |
      {
        "id": ${path.userId},
        "name": "User${path.userId}"
      }
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
    body: |
      [3, 2, 1]

- when: params.order == 'asc'
  response:
    headers:
      content-type: application/json
    body: |
      [1, 2, 3]
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
    body: |
      {
        "id": ${path.userId},
        "name": "${tables.userName}",
        "age": ${tables.age}
      }
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
