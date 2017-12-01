package org.hidetake.stubyaml.test.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import java.nio.charset.Charset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResponseBodySpec extends Specification {
    static final SHIFT_JIS_CHARSET = Charset.forName('Shift_JIS')

    @Autowired WebTestClient client

    def 'Response body should be set regardless of accept'() {
        when:
        def response = client.get()
            .uri('/users')
            .accept(MediaType.TEXT_PLAIN)
            .exchange() as WebTestClient.ResponseSpec

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
        response.expectBodyList(User)
            .isEqualTo([new User(1, 'Foo'), new User(2, 'Bar')])
    }

    def 'Response body should be given charset'() {
        given:
        def body = 'あいうえお'

        when:
        def response = client.post()
            .uri('/features/request-body-value?charset=Shift_JIS')
            .syncBody(body)
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(new MediaType(MediaType.TEXT_PLAIN, SHIFT_JIS_CHARSET))
        response.expectBody(String).isEqualTo(body)
    }

    def 'Response body should be given charset if it is application/json'() {
        when:
        def response = client.get()
            .uri('/features/response-json-charset?charset=Shift_JIS')
            .exchange() as WebTestClient.ResponseSpec

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(new MediaType(MediaType.APPLICATION_JSON, SHIFT_JIS_CHARSET))
        response.expectBody(String).isEqualTo('[{"name":"あいうえお","id":1}]')
    }
}
