package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import spock.lang.Specification

@SpringBootTest
class ReviewControllerSpec extends Specification {
    private static final long REVIEW_PRODUCT_ID = 13
    private static final long NO_REVIEW_PRODUCT_ID = 1000
    Review REVIEW = new Review(productId: 1, averageReviewScore: 7.4, numberOfReviews: 2000)

    @Autowired
    private ReviewController reviewController
    @Autowired
    private ReviewRepository reviewRepository

    void "Application has ReviewController"() {
        expect:
        this.reviewController
    }

    void "ReviewController saves new review"() {
        when: "ReviewController is asked to save the review"
        this.reviewController.save(REVIEW)

        then: "ReviewController finds the review"
        Review readReview = this.reviewController.findByProductId(REVIEW.productId)
        REVIEW.averageReviewScore == readReview.averageReviewScore
        REVIEW.numberOfReviews == readReview.numberOfReviews
    }

    void "ReviewController updates review"() {
        given: "A new average review score"
        Float newAverageReviewScore = 10.0

        when: "ReviewController is asked to save a review with existing product id and new average review score"
        Review readReview = this.reviewController.findByProductId(REVIEW_PRODUCT_ID)
        Review newReview = readReview.clone() as Review
        newReview.averageReviewScore = newAverageReviewScore
        this.reviewController.save(newReview)

        then: "The review has the new average review score"
        newAverageReviewScore == this.reviewController.findByProductId(REVIEW_PRODUCT_ID).averageReviewScore
    }

    void "ReviewController deletes review"() {
        when: "ReviewController is asked to delete review"
        this.reviewController.deleteByProductId(REVIEW_PRODUCT_ID)

        and: "The review is read"
        this.reviewController.findByProductId(REVIEW_PRODUCT_ID)

        then: "The review is not found"
        ResponseStatusException responseStatusException = thrown()
        HttpStatus.NOT_FOUND == responseStatusException.status
    }

    void "ReviewController responds 404 when asked to find unexisting review"(){
        when: "ReviewController is asked to find unexisting review"
        this.reviewController.findByProductId(NO_REVIEW_PRODUCT_ID)

        then: "ReviewController responds 404"
        ResponseStatusException responseStatusException = thrown()
        HttpStatus.NOT_FOUND == responseStatusException.status
    }

    void "ReviewController responds 500 when it cannot save review"(){
        given: "ReviewController has stubbed ReviewRepository that cannot save review"
        this.reviewController.reviewRepository = Mock(ReviewRepository)
        this.reviewController.reviewRepository.save(REVIEW) >> {
            throw new Exception("Cannot save")
        }

        when: "ReviewController is asked to save review"
        this.reviewController.save(REVIEW)

        then: "ReviewController responds 500"
        ResponseStatusException responseStatusException = thrown()
        HttpStatus.INTERNAL_SERVER_ERROR == responseStatusException.status

        cleanup:
        this.reviewController.reviewRepository = this.reviewRepository
    }

    void "ReviewController responds 404 when asked to delete unexisting review"(){
        when: "ReviewController is asked to delete unexisting review"
        this.reviewController.deleteByProductId(NO_REVIEW_PRODUCT_ID)

        then: "ReviewController responds 404"
        ResponseStatusException responseStatusException = thrown()
        HttpStatus.NOT_FOUND == responseStatusException.status
    }
}
