package org.hidetake.stubyaml.test.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.Charset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequestBodySpec extends Specification {

    static final SHIFT_JIS_CHARSET = Charset.forName('Shift_JIS')

    @Autowired WebTestClient client

    def 'Request body should be a Map if it is multipart/form-data'() {
        given:
        def image = new FileSystemResource('src/test/resources/data/users/{userId}/photo.jpg')
        def requestBody = new LinkedMultiValueMap()
        requestBody.add('file', image)

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .body(BodyInserters.fromMultipartData(requestBody))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('Map')
    }

    def 'Request body should be a Map if it is application/x-www-form-urlencoded'() {
        given:
        def requestBody = new LinkedMultiValueMap()
        requestBody.add('id', '10')

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .body(BodyInserters.fromFormData(requestBody))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('Map')
    }

    def 'Request body should be a Map if it is application/json'() {
        given:
        def requestBody = new User(3)

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(requestBody))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('Map')
    }

    def 'Request body should be a Map if it is application/json and charset is not UTF-8'() {
        given:
        def requestBody = '{"name":"あいうえお"}'.getBytes(SHIFT_JIS_CHARSET)

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .contentType(new MediaType(MediaType.APPLICATION_JSON, SHIFT_JIS_CHARSET))
            .body(BodyInserters.fromPublisher(Mono.just(requestBody), byte[]))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('Map')
    }

    @Unroll
    def 'Request body should be a Map if it is #contentType'() {
        given:
        def requestBody = '''\
<?xml version="1.0" encoding="utf-8"?>
<user><id>3</id></user>
'''

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .contentType(contentType)
            .body(BodyInserters.fromObject(requestBody))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('Map')

        where:
        contentType << [MediaType.APPLICATION_XML, MediaType.TEXT_XML]
    }

    @Unroll
    def 'Request body should be a Map if it is #contentType and charset is not UTF-8'() {
        given:
        def requestBody = '<name>あいうえお</name>'.getBytes(SHIFT_JIS_CHARSET)

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .contentType(new MediaType(contentType, SHIFT_JIS_CHARSET))
            .body(BodyInserters.fromPublisher(Mono.just(requestBody), byte[]))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('Map')

        where:
        contentType << [MediaType.APPLICATION_XML, MediaType.TEXT_XML]
    }

    def 'Request body should be a String if it is text/plain'() {
        given:
        def requestBody = 'just text'

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .contentType(MediaType.TEXT_PLAIN)
            .body(BodyInserters.fromObject(requestBody))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('String')
    }

    def 'Request body should be null if it is image/jpeg'() {
        given:
        def image = new FileSystemResource('src/test/resources/data/users/{userId}/photo.jpg')

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .body(BodyInserters.fromResource(image))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('null')
    }

}
