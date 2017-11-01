package org.hidetake.stubyaml.test.integration

import org.hidetake.stubyaml.test.integration.model.Group
import org.hidetake.stubyaml.test.integration.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
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

    def 'POST /users should return a user with placeholder replaced'() {
        when:
        def users = restTemplate.postForEntity('/users', new User(5, 'Baz'), User)

        then:
        users.statusCode == HttpStatus.OK
        users.body.id == 5
        users.body.name == 'Baz'
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

    def 'GET /authorities should return 500'() {
        when:
        def response = restTemplate.getForEntity('/authorities', String)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def 'GET /empty should return 404'() {
        when:
        def response = restTemplate.getForEntity('/empty', String)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }
}
