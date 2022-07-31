package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ReviewServiceSpec extends Specification {
    private static final String EXISTING_REVIEW_PRODUCT_ID = "AA0001"

    @Autowired
    private ReviewService reviewService

    def "Application has ReviewService"() {
        expect:
        this.reviewService
    }

    def "ReviewService saves and reads review"() {
        given: "A review"
        Review testReview = new Review(productId: "AB1234", averageReviewScore: 7.4, numberOfReviews: 2000)

        when: "The review is saved"
        this.reviewService.save testReview

        then: "The review can be read"
        Review readReview = this.reviewService.findByProductId(testReview.productId)
        testReview.averageReviewScore == readReview.averageReviewScore
        testReview.numberOfReviews == readReview.numberOfReviews
    }

    def "ReviewService updates review"() {
        given: "A new average review score"
        Float newAverageReviewScore = 10.0

        when: "A review with existing product id is saved with the new average review score"
        Review readReview = this.reviewService.findByProductId(EXISTING_REVIEW_PRODUCT_ID)
        Review newReview = readReview.clone() as Review
        newReview.averageReviewScore = newAverageReviewScore
        this.reviewService.save(newReview)

        then: "The review has the new average review score"
        newAverageReviewScore == this.reviewService.findByProductId(EXISTING_REVIEW_PRODUCT_ID).averageReviewScore
    }

    def "ReviewService deletes review"() {
        when: "A review with existing product id is deleted"
        this.reviewService.deleteByProductId(EXISTING_REVIEW_PRODUCT_ID)

        then: "The review is not in the data storage"
        ! this.reviewService.findByProductId(EXISTING_REVIEW_PRODUCT_ID)
    }
}
