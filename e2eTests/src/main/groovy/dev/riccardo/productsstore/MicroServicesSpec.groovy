package dev.riccardo.productsstore

import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class MicroServicesSpec extends Specification {
    def "Prouct service aggregates product and review"() {
        given: "An http client"
        RestTemplate testRestTemplate = new RestTemplate()

        and: "An expected aggregation response"
        AggregateResponse expectedResponse = new AggregateResponse(
            id: 18,
            name: "Agua y refrescos",
            numberOfReviews: 5000,
            averageReviewScore: 10.0
        )

        when: "The http client asl product service for a product"
        AggregateResponse respose = testRestTemplate.getForObject("http://localhost:8080/product/18", AggregateResponse)

        then: "The product and its review are retrieved"
        expectedResponse.id == respose.id
        expectedResponse.name == respose.name
        expectedResponse.numberOfReviews == respose.numberOfReviews
        expectedResponse.averageReviewScore == respose.averageReviewScore
    }
}
