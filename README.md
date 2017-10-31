# stubyaml

A YAML based HTTP stub server for API testing.
Built on Spring Boot.


## Getting Started

Download stubyaml.jar and create `data/users.get.yaml` with following.

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

Run the application.

```
./gradlew bootRun
```

Call API as follows.

```
curl -v http://localhost:8080/users
```


## Configuration

Suppress request and response log for improving performance.

```
export NO_REQUEST_RESPONSE_LOG=1
./gradlew bootRun
```


## Examples

### Handle various HTTP methods

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


### Serve various contents

Here is an example of serving XML.

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

Note that body is not validated and possibly illegal content may be sent.


### Use path variables

Underscore braced string in file path is treated as a path variable.
For example, create `data/users/_userId_.get.yaml` for handling GET of `users/1`, `users/2` and so on.


### Use script

`${key}` in headers and body is parsed as a Groovy script.
You can access to following objects via each prefix.

Prefix      | Object
------------|-------
`request.`  | `HttpServletRequest` object bound to current request
`path.`     | Path variables
`params.`   | Request parameters
`body.`     | Request body

For example, create `data/users/_userId_.get.yaml` with following.

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

The stub will serve following body on the request `GET /users/100`.

```json
{
  "id": 100,
  "name": "User100"
}
```


## Pattern match

A YAML file has one or more rules.
The stub evaluates `when` and returns the first matched `response`.

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


## Replace variable by table lookup

For example, create `data/users/_userId_.get.yaml` with following rule.

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

The stub will serve following body on the request `GET /users/1`:

```json
{
  "id": 1,
  "name": "Foo",
  "age": 35
}
```

And the request `GET /users/2`:

```json
{
  "id": 2,
  "name": "Bar",
  "age": 100
}
```
