package dev.riccardo.productsstore

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
@RestController
class ProductController {
    @Autowired
    private ProductService productService

    static void main(String[] args) {
        SpringApplication.run(ProductController, args)
    }

    @GetMapping("/product/{productId}")
    AggregateResponse findProductById(@PathVariable Long productId) {
        this.productService.findProductById(productId)
    }
}


@Service
class ProductService {
    static final String PRODUCT_API_URL = "https://tienda.mercadona.es/api/categories/{productId}"
    private Logger logger = LoggerFactory.getLogger(ProductService)
    @Value('#{"http://" + "${review-service.domain-name}" + "/review/{productId}"}')
    String reviewApiUrl
    RestTemplate restTemplate = new RestTemplate()
    @Autowired
    ResponseStatusExceptionWrapper responseStatusExceptionWrapper

    AggregateResponse findProductById(@PathVariable Long productId) {
        this.responseStatusExceptionWrapper.forward {
            this.logRequest(PRODUCT_API_URL, productId)
            Product product = this.restTemplate.getForObject(PRODUCT_API_URL, Product, [productId: productId])
            this.logRequest(reviewApiUrl, productId)
            Review review = this.restTemplate.getForObject(reviewApiUrl, Review, [productId: productId])
            if(review.productId != product.id) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fetched review productId ${review.productId} does not match fetched product id ${product.id}")
            }
            new AggregateResponse([
                id: product.id,
                name: product.name,
                averageReviewScore: review.averageReviewScore,
                numberOfReviews: review.numberOfReviews
            ])
        }
    }

    private void logRequest(String url, Long productId) {
        this.logger.info "Requesting ${url.replaceAll(/\{productId}/, productId as String)}"
    }
}

class AggregateResponse {
    Long id
    String name
    Float averageReviewScore
    Long numberOfReviews

    @Override
    String toString() {
        "Product [${this.id}]: ${this.name}, ${this.averageReviewScore} score over ${this.numberOfReviews} reviews"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Product {
    Long id
    String name

    @Override
    String toString() {
        "Product [${this.id}]: ${this.name}"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Review {
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

@Component
class ResponseStatusExceptionWrapper {
    public <T> T forward(Closure <T> body) {
        try {
            body.call()
        } catch(HttpClientErrorException | HttpServerErrorException httpServerErrorException) {
            throw new ResponseStatusException(
                httpServerErrorException.statusCode,
                "Response from upstream service: ${httpServerErrorException.message}",
                httpServerErrorException
            )
        }
    }
}

