package dev.riccardo.productsstore

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

import javax.persistence.Entity
import javax.persistence.Id

@SpringBootApplication
@RestController
class ReviewController {
    private Logger logger = LoggerFactory.getLogger(ReviewController)
    @Autowired
    ReviewRepository reviewRepository
    @Autowired
    WithResponseStatusException withResponseStatusException

    static void main(String[] args) {
        SpringApplication.run(ReviewController, args)
    }

    @GetMapping("/review/{productId}")
    Review findByProductId(@PathVariable Long productId) {
        this.reviewRepository.findById(productId).orElseThrow {
            new ResponseStatusException(HttpStatus.NOT_FOUND, "No review available for product with id ${productId}")
        }
    }

    @PostMapping("/review")
    Review save(@RequestBody Review review) {
        logger.info "Received review: ${review}"
        this.withResponseStatusException.internalServerError {
            this.reviewRepository.save(review)
        }
    }

    @DeleteMapping("/review/{productId}")
    void deleteByProductId(@PathVariable Long productId) {
        logger.info "Deleting review: ${productId}"
        this.withResponseStatusException(HttpStatus.NOT_FOUND, null) {
            this.reviewRepository.deleteById productId
        }
    }
}

interface ReviewRepository extends CrudRepository<Review, Long> {}

@Entity
class Review implements Cloneable {
    @Id
    Long productId
    Float averageReviewScore
    Long numberOfReviews

    @Override
    String toString() {
        "[${this.productId}] ${this.averageReviewScore} score over ${this.numberOfReviews} reviews"
    }
}

@Component
class WithResponseStatusException {
    public <T> T internalServerError(Closure<T> body) {
        this.call(HttpStatus.INTERNAL_SERVER_ERROR, null, body)
    }

    public <T> T call(HttpStatus status, String reason, Closure <T> body) {
        try {
            body.call()
        } catch(Exception exception) {
            exception.printStackTrace()
            throw new ResponseStatusException(status, reason ?: exception.message ?: (exception.class as String), exception)
        }
    }
}


