# stubyaml

YAML based HTTP stub server for API testing.
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

Run stubyaml.jar.

```
java -jar stubyaml.jar
```

Call API as follows.

```
curl -v http://localhost:8080/users
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


### Use placeholders

`${key}` is treated as a placeholder and replaced to matched path variable, request parameter or request body.

For example, create `data/users/_userId_.get.yaml` with following.

```yaml
- response:
    headers:
      content-type: application/json
    body: |
      {
        "id": ${userId},
        "name": "User#${userId}"
      }
```

The stub will serve following content on the request `GET /users/100`.

```json
{
  "id": 100,
  "name": "User#100"
}
```
