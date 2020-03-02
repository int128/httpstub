package org.hidetake.stubyaml.test.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.FileSystemResource
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StubSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    def 'GET /users should return array of users'() {
        when:
        def users = restTemplate.getForEntity('/users', User[])

        then:
        users.statusCode == HttpStatus.OK
        users.body.length == 2
        users.body[0] == new User(1, 'Foo')
        users.body[1] == new User(2, 'Bar')
        !users.headers.getFirst('x-uuid').empty
        users.headers.get('x-uid') == ['10000', '20000']
    }

    @Unroll
    def 'GET /users/#id should return a user with placeholder replaced'() {
        when:
        def user = restTemplate.getForEntity('/users/{id}', User, [id: id])

        then:
        user.statusCode == HttpStatus.OK
        user.body.id == id
        user.body.name == name
        user.body.age == age

        where:
        id | name   | age
        1  | 'Foo'  | 35
        2  | 'Bar'  | 100
        3  | 'Baz'  | 3
    }

    def 'POST /users should return a user with placeholder replaced with JSON data'() {
        when:
        def users = restTemplate.postForEntity('/users', new User(5, 'Baz', null, 100, true), User)

        then:
        users.statusCode == HttpStatus.OK
        users.body.id == 5
        users.body.name == 'Baz'
        users.body.description == 'user#5'
        users.body.active
    }

    def 'POST /users should return a user with placeholder replaced with form data'() {
        given:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        def body = new LinkedMultiValueMap()
        body.add('id', '3')
        body.add('name', 'Foo')
        body.add('active', 'true')
        def request = new HttpEntity(body, headers)

        when:
        def users = restTemplate.postForEntity('/users', request, User)

        then:
        users.statusCode == HttpStatus.OK
        users.body.id == 3
        users.body.name == 'Foo'
        users.body.description == 'user#3'
        users.body.active
    }

    @Unroll
    def 'DELETE /users/#id should return 204'() {
        when:
        def response = restTemplate.exchange('/users/{id}', HttpMethod.DELETE, null, String, [id: id])

        then:
        response.statusCode == HttpStatus.NO_CONTENT
        !response.hasBody()

        where:
        id << [1, 2]
    }

    def 'GET /groups/1 should return array of groups (nested object)'() {
        when:
        def groups = restTemplate.getForEntity('/groups/1', Group)

        then:
        groups.statusCode == HttpStatus.OK
        groups.body == new Group(1, 'Example Group', [new User(1, "Foo")])
    }

    @Unroll
    def 'GET /numbers?order=#order should return condition matched response'() {
        when:
        def response = restTemplate.getForEntity("/numbers?order=$order", List)

        then:
        response.statusCode == HttpStatus.OK
        response.body == body

        where:
        order   | body
        'asc'   | [1, 2, 3]
        'desc'  | [3, 2, 1]
    }

    def 'GET /users/2/photo should return an image'() {
        when:
        def photo = restTemplate.getForEntity('/users/2/photo', byte[])

        then:
        photo.statusCode == HttpStatus.OK
        photo.headers.getContentType() == MediaType.IMAGE_JPEG
    }

    def 'POST /users/2/photo should accept multipart file upload'() {
        given:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.MULTIPART_FORM_DATA)
        def body = new LinkedMultiValueMap()
        def image = new FileSystemResource('src/test/resources/data/users/{userId}/photo.jpg')
        body.add('file', image)
        def request = new HttpEntity(body, headers)

        when:
        def photo = restTemplate.postForEntity('/users/2/photo', request, Photo)

        then:
        photo.statusCode == HttpStatus.OK
        !photo.body.id.empty
        photo.body.user.id == 2
        photo.body.filename == 'photo.jpg'
        photo.body.contentType == 'image/jpeg'
    }

    def 'GET /features/status-code should return 500'() {
        when:
        def response = restTemplate.getForEntity('/features/status-code', String)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def 'GET /features/empty-rule should return 404'() {
        when:
        def response = restTemplate.getForEntity('/features/empty-rule', String)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def 'GET /clients should return array of clients with new stub spec'() {
        when:
        def clients = restTemplate.getForEntity('/clients', Map[])

        then:
        clients.statusCode == HttpStatus.OK
        clients.body.length == 1
        clients.body[0] == ['id': 1, 'name': 'client1']
        clients.headers.size() == 2
        ['application/json'] in clients.headers.values()
    }

    def 'POST /request-body-array should return array from request body'() {
        given:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def body = [
            [
                data: [
                    mykey: "myvalue"
                ]
            ]
        ]
        def request = new HttpEntity(body, headers)

        when:
        def response = restTemplate.postForEntity('/features/request-body-array', request, Map)

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 1
        response.body == ['data-from-backend': 'myvalue']
        response.headers.size() == 2
        ['application/json'] in response.headers.values()
    }

    def 'POST /response-json-serialize should return serialized json in output field'() {
        given:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def body = [
            data: [
                mykey2: "myvalue2"
            ]
        ]
        def request = new HttpEntity(body, headers)

        when:
        def response = restTemplate.postForEntity('/features/response-json-serialize', request, Map)

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 1
        response.body == ['data-from-backend': [mykey2: "myvalue2"]]
        response.headers.size() == 2
        ['application/json'] in response.headers.values()
    }

}
