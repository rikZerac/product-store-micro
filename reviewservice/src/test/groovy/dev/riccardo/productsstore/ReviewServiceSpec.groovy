package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ReviewServiceSpec extends Specification {
    @Autowired
    private ReviewService reviewService

    @Autowired
    private ReviewRepository reviewRepository

    def "Application has ReviewService"() {
        expect:
        this.reviewService
    }

    def "ReviewRepository saves and reads review"() {
        given:
        Review testReview = new Review(productId: "AB1234", averageReviewScore: 7.4, numberOfReviews: 2000)

        when:
        this.reviewRepository.save testReview

        then:
        Review readReview = this.reviewRepository.findByProductId(testReview.productId)
        testReview.averageReviewScore == readReview.averageReviewScore
        testReview.numberOfReviews == readReview.numberOfReviews
    }
}
