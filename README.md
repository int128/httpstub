# httpstub [![CircleCI](https://circleci.com/gh/int128/httpstub.svg?style=shield)](https://circleci.com/gh/int128/httpstub) [![Gradle Status](https://gradleupdate.appspot.com/int128/httpstub/status.svg)](https://gradleupdate.appspot.com/int128/httpstub/status)

This is a HTTP stub server for integration test with external APIs.

Key features:

- Single JAR
- Declarative API definition using YAML
- Template rendering and pattern matching using Groovy
- File watcher
- Gradle plugin


## Getting Started

Download [the latest release](https://github.com/int128/httpstub/releases).
Java 11 or later is required.

Define a route as follows:

```sh
mkdir -p data
vim data/users.get.yaml
```

```yaml
# data/users.get.yaml
version: v1.1
rules: 
  - response:
      headers:
        content-type: application/json
      body:
        - id: 1
          name: Foo
        - id: 2
          name: Bar
```

Or even simpler, with an old declaration

```yaml
# data/users.get.yaml
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
java -jar httpstub.jar
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

The stub will reload YAML files if they have been changed or new one has been created.


## Options

### Logging

You can write log to a file.

By following option, the stub writes log to `logs/spring.log`, rotates when it reaches 10MB and keeps up to 8 files.
See [Spring Boot features: Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html) for more.

```
# Command line option
java -jar httpstub.jar --logging.path=logs

# Environment variable
export LOGGING_PATH=logs
java -jar httpstub.jar
```


### Request and response logging

The stub shows following log for each request.

```
2017-12-05 10:44:20.042  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : > GET /users
2017-12-05 10:44:20.043  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : > Host: localhost:8080
2017-12-05 10:44:20.044  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : > User-Agent: curl/7.54.0
2017-12-05 10:44:20.044  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : > Accept: */*
2017-12-05 10:44:20.044  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : >
2017-12-05 10:44:20.047  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : < 200 OK
2017-12-05 10:44:20.048  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : < content-type: application/json
2017-12-05 10:44:20.049  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : < x-uuid: 1992cb3d-7bbf-4c2e-aa65-a19fa656f77e
2017-12-05 10:44:20.050  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : <
2017-12-05 10:44:20.050  INFO 19694 --- [ctor-http-nio-2] o.h.stubyaml.app.RequestResponseLogger   : < [{"name":"Foo","id":1},{"name":"Bar","id":2}]
```

You can turn off logging by creating `data/config.yaml`:

```yaml
logging:
  headers: false
  body: false
```


## Recipes

### HTTP methods

Specify HTTP method in the extension part of filename.
For example, create a route file `data/users.post.yaml` for handling POST method.
Following methods are supported.

- GET
- HEAD
- POST
- PUT
- PATCH
- DELETE
- OPTIONS
- TRACE


### Path variables

A braced string in the file path is treated as a path variable.
For example, create `/users/{userId}.get.yaml` for handling `/users/1`, `/users/2` and so on.


### Response header

You can set pairs of key and value to the headers. The value must be a string and is parsed as a template (see also the later section).

```yaml
- response:
    headers:
      content-type: text/plain
      x-uuid: "1234567890"
```

You can set multiple values.

```yaml
- response:
    headers:
      set-cookie:
        - sessionId=38afes7a8
        - id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT
```


### Response body

You can serve a text body as follows:

```yaml
version: v1.1
rules:
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

You can serve a JSON body as follows:

```yaml
- response:
    headers:
      content-type: application/json
    body:
      id: 1
      name: Alice
```

If a character set is specified in the `content-type` header, the response body is encoded to the character set.

```yaml
version: v1.1
rules:
  - response:
      headers:
        content-type: text/plain;charset=Shift_JIS
      body: あいうえお
```

You can serve a file content as follows:

```yaml
version: v1.1
rules:
  - response:
      headers:
        content-type: image/jpeg
      file: photo.jpg
```


### Template

Following values are parsed as a Groovy template:

- Response header value
- Response body (`body`)
- Response filename (`file`)
- Table key (`key` of `tables`)

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
`application/xml` and subset, `text/xml` and subset | `Map<String, Object>`
`text/*` | `String`
Otherwise | `null`

For example, create `/users/{userId}.get.yaml` as following:

```yaml
version: v1.1
rules:
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
The stub evaluates each `when` of all rules and returns the first matched `response`.

Here is the example of `/numbers.get.yaml` as follows:

```yaml
version: v1.1
rules:
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


### Constants

Define constants in `data/config.yaml`:

```yaml
constants:
  today: "2017-12-01"
```

You can use constants in a route YAML:

```yaml
- response:
    headers:
      content-type: application/json
    body:
      - id: 1
        name: Foo
        registeredDate: ${constants.today}
```


### Tables

Tables are usuful for data variation testing.

Let's see the example.

Request condition: `path.userId` | Response variable: `tables.userName` | Response variable: `tables.age`
---------------------------------|--------------------------------------|--------------------------------
1 | Foo | 35
2 | Bar | 100
3 | Baz | 3

Create `/users/{userId}.get.yaml` with following rule.

```yaml
version: v1.1
rules:
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


## Delay

Use `delay` attribute in milliseconds to simulate network latency.

For example, create `/users.post.yaml` as following:

```yaml
version: v1.1
rules:
  - response:
      delay: 500
      headers:
        content-type: application/json
      body:
        id: 1
```

Send the request `POST /users` and the stub will return a response after 500 ms.

## Gradle plugin

You can connect this application as a gradle plugin to your's e2e tests or ci.   
Configure your project as show below

```groovy
buildscript {
	dependencies {
		classpath 'org.hidetake.stubyaml:gradle-plugin:1.0.0-SNAPSHOT'
	}
}

apply plugin: 'org.hidetake.stubyaml'

httpstub {
	serverPort = '8078'  //port where httpstub will run
	stubData = 'src/test/resources/stubs' //folder where stored configs
}
```

`gradle httpstubStart` - to start stub server   
`gradle httpstubStop` - to stop stub server

## Troubleshooting
If you see in logs next exception `java.io.IOException: User limit of inotify watches reached` don't afraid
just execute `echo 16384 | sudo tee /proc/sys/fs/inotify/max_user_watches` on your linux machine 

## Versioning

After 12.2019 for backward compatibilities added version to config files. Config files introduction of a versioning system for further development and addition of new features.

Variable    | Object
------------|-------
`v1.0`      | Old version of project
`v1.1`      | Added RuleContainer

## Contributions

This is an open source software.
Feel free to open issues and pull requests.
