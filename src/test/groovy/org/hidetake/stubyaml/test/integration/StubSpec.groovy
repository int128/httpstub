package org.hidetake.stubyaml.test.integration

import org.hidetake.stubyaml.test.integration.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
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

        where:
        id | name
        1  | 'User1'
        2  | 'User2'
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

    def 'GET /authorities should return 500'() {
        when:
        def response = restTemplate.getForEntity('/authorities', String)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
    }
}
