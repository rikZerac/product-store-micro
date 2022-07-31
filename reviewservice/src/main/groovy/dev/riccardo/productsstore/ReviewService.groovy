package dev.riccardo.productsstore

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.repository.CrudRepository
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Review implements Cloneable{
    @Id
    String productId
    Float averageReviewScore
    Long numberOfReviews

    Boolean asBoolean() {
        this.productId !=null
    }

    @Override
    String toString() {
        "[${this.productId}] ${this.averageReviewScore} score over ${this.numberOfReviews} reviews"
    }
}

interface ReviewRepository extends CrudRepository<Review, String> {

}

@SpringBootApplication
@RestController
class ReviewService {
    private Logger logger = LoggerFactory.getLogger(ReviewService.class)

    @Autowired
    private ReviewRepository reviewRepository

    static void main(String[] args) {
        SpringApplication.run(ReviewService, args)
    }

    @GetMapping("/review/{productId}")
    Review findByProductId(@PathVariable String productId) {
        this.reviewRepository.findById(productId).orElse(new Review())
    }

    @PostMapping("/review")
    Review save(@RequestBody Review review) {
        logger.info "Received review: ${review}"
        this.reviewRepository.save(review)
    }

    @DeleteMapping("/review/{productId}")
    def deleteByProductId(@PathVariable String productId) {
        logger.info "Deleting review: ${productId}"
        this.reviewRepository.deleteById productId
    }
}
