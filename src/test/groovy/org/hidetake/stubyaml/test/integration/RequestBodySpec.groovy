package org.hidetake.stubyaml.test.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequestBodySpec extends Specification {
    @Autowired WebTestClient client

    def 'Request body should be a Map if it is multipart/form-data'() {
        given:
        def requestBody = new LinkedMultiValueMap()
        requestBody.add('file', new ClassPathResource('/photo.jpg'))

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

    def 'Request body should be a Map if it is application/xml'() {
        given:
        def requestBody = '''\
<?xml version="1.0" encoding="utf-8"?>
<user><id>3</id></user>
'''

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .contentType(MediaType.APPLICATION_XML)
            .body(BodyInserters.fromObject(requestBody))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('Map')
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
        def requestBody = new ClassPathResource('/photo.jpg')

        when:
        def response = client.post()
            .uri('/features/request-body-type')
            .body(BodyInserters.fromResource(requestBody))
            .exchange()

        then:
        response.expectStatus().isOk()
        response.expectHeader().contentType(MediaType.TEXT_PLAIN)
        response.expectBody(String).isEqualTo('null')
    }
}
