package dev.riccardo.productsstore

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class MicroServicesSpec extends Specification {
    private static final TestRestTemplate TEST_REST_TEMPLATE = new TestRestTemplate().withBasicAuth("lee", "rock")

    def "Review service reads review"() {
        given: "An existing review"
        Review expectedReview = new Review(
            productId: 13,
            numberOfReviews: 800,
            averageReviewScore: 4.0
        )

        when: "The http client ask review service for the expected review"
        ResponseEntity<Review> responseEntity = TEST_REST_TEMPLATE.getForEntity("http://localhost/review/{productId}", Review, [productId: expectedReview.productId])

        then: "The expected review is retrieved"
        HttpStatus.OK == responseEntity.statusCode
        expectedReview.productId == responseEntity.body.productId
        expectedReview.numberOfReviews == responseEntity.body.numberOfReviews
        expectedReview.averageReviewScore == responseEntity.body.averageReviewScore
    }

    def "Review service saves review"() {
        given: "A review"
        Review review = new Review(
            productId: 25,
            numberOfReviews: 1000,
            averageReviewScore: 8.5
        )

        when: "The http client ask review service to save the review"
        ResponseEntity<Review> responseEntity = TEST_REST_TEMPLATE.postForEntity("http://localhost/review", review, Review)

        then: "The saved review is the one expected"
        HttpStatus.OK == responseEntity.statusCode
        responseEntity.body.productId == review.productId
        responseEntity.body.numberOfReviews == review.numberOfReviews
        responseEntity.body.averageReviewScore == review.averageReviewScore
    }

    def "Review service deletes review"() {
        given: "An review product id"
        Long productId = 18

        when: "The http client ask review service to delete the review for the product id"
        TEST_REST_TEMPLATE.delete("http://localhost/review/{productId}", [productId: productId])

        and: "The review is read"
        ResponseEntity<Review> responseEntity = TEST_REST_TEMPLATE.getForEntity("http://localhost/review/{productId}", Review, [productId: productId])

        then: "The review is not found"
        HttpStatus.NOT_FOUND == responseEntity.statusCode
    }

    def "Product service aggregates product and review"() {
        given: "An expected aggregation response"
        AggregateResponse expectedResponse = new AggregateResponse(
            id: 13,
            name: "Arroz, legumbres y pasta",
            numberOfReviews: 800,
            averageReviewScore: 4.0
        )

        when: "The http client ask product service for the product of the expected response"
        ResponseEntity<AggregateResponse> responseEntity = TEST_REST_TEMPLATE.getForEntity("http://localhost:8080/product/{productId}", AggregateResponse, [productId: expectedResponse.id])

        then: "The expected response is retrieved"
        HttpStatus.OK == responseEntity.statusCode
        expectedResponse.id == responseEntity.body.id
        expectedResponse.name == responseEntity.body.name
        expectedResponse.numberOfReviews == responseEntity.body.numberOfReviews
        expectedResponse.averageReviewScore == responseEntity.body.averageReviewScore
    }
}
