package dev.riccardo.productsstore

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import javax.persistence.Entity
import javax.persistence.Id

@SpringBootApplication
@RestController
class ReviewController {
    private Logger logger = LoggerFactory.getLogger(ReviewController)
    @Autowired
    private ReviewRepository reviewRepository

    static void main(String[] args) {
        SpringApplication.run(ReviewController, args)
    }

    @Bean
    SecurityFilterChain configureHttpSecurity(HttpSecurity httpSecurity) {
        httpSecurity
            .authorizeHttpRequests {
                it.with {
                    antMatchers(HttpMethod.GET, "/**").permitAll()
                    anyRequest().authenticated()
                }
            }
        // 'username' and 'password' credentials fields names, /login path for login page
            .formLogin(Customizer.withDefaults())
            .csrf().disable()
            .build()
    }

    @GetMapping(["/", "/home"])
    String sayWelcome() {
        "Welcome to this awesome products store!"
    }

    @GetMapping("/review/{productId}")
    Review findByProductId(@PathVariable Long productId) {
        this.reviewRepository.findById(productId).orElse(new Review())
    }

    @PostMapping("/review")
    Review save(@RequestBody Review review) {
        logger.info "Received review: ${review}"
        this.reviewRepository.save(review)
    }

    @DeleteMapping("/review/{productId}")
    def deleteByProductId(@PathVariable Long productId) {
        logger.info "Deleting review: ${productId}"
        this.reviewRepository.deleteById productId
    }
}

interface ReviewRepository extends CrudRepository<Review, Long> {}

@Entity
class Review implements Cloneable {
    @Id
    Long productId
    Float averageReviewScore
    Long numberOfReviews

    Boolean asBoolean() {
        this.productId != null
    }

    @Override
    String toString() {
        "[${this.productId}] ${this.averageReviewScore} score over ${this.numberOfReviews} reviews"
    }
}


