package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.repository.CrudRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Review {
    @Id
    String productId
    Float averageReviewScore
    Long numberOfReviews

    @Override
    String toString() {
        "[${this.productId}] ${this.averageReviewScore} score over ${this.numberOfReviews} reviews"
    }
}

interface ReviewRepository extends CrudRepository<Review, String> {
    Review findByProductId(String productId)
}

@SpringBootApplication
@RestController
class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository

    static void main(String[] args) {
        SpringApplication.run(ReviewService, args)
    }

    @GetMapping("/review/{productId}")
    Review getReview(@PathVariable String productId) {
        this.reviewRepository.findByProductId productId
    }
}
