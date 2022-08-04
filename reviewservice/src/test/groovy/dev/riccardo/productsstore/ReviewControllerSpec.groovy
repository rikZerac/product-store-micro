package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ReviewControllerSpec extends Specification {
    private static final long EXISTING_REVIEW_PRODUCT_ID = 13

    @Autowired
    private ReviewController reviewController

    void "Application has ReviewController"() {
        expect:
        this.reviewController
    }

    void "ReviewController saves and reads review"() {
        given: "A review"
        Review testReview = new Review(productId: 1, averageReviewScore: 7.4, numberOfReviews: 2000)

        when: "The review is saved"
        this.reviewController.save testReview

        then: "The review can be read"
        Review readReview = this.reviewController.findByProductId(testReview.productId)
        testReview.averageReviewScore == readReview.averageReviewScore
        testReview.numberOfReviews == readReview.numberOfReviews
    }

    void "ReviewController updates review"() {
        given: "A new average review score"
        Float newAverageReviewScore = 10.0

        when: "A review with existing product id is saved with the new average review score"
        Review readReview = this.reviewController.findByProductId(EXISTING_REVIEW_PRODUCT_ID)
        Review newReview = readReview.clone() as Review
        newReview.averageReviewScore = newAverageReviewScore
        this.reviewController.save(newReview)

        then: "The review has the new average review score"
        newAverageReviewScore == this.reviewController.findByProductId(EXISTING_REVIEW_PRODUCT_ID).averageReviewScore
    }

    void "ReviewController deletes review"() {
        when: "A review with existing product id is deleted"
        this.reviewController.deleteByProductId(EXISTING_REVIEW_PRODUCT_ID)

        then: "The review is not in the data storage"
        ! this.reviewController.findByProductId(EXISTING_REVIEW_PRODUCT_ID)
    }
}
