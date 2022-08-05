package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import spock.lang.Specification

@SpringBootTest
class ProductControllerSpec extends Specification {
    private static final Long PRODUCT_ID = 13
    private static final Product PRODUCT = new Product(id: PRODUCT_ID, name: "Pop corn")
    private static final Review REVIEW = new Review(productId: PRODUCT_ID, averageReviewScore: 5.0, numberOfReviews: 10)
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND

    @Autowired
    private ProductController productController
    @Autowired
    private ProductService productService

    void "Application has ProductController"() {
        expect:
        this.productController
    }

    void setup() {
        this.productService.restTemplate = Mock(RestTemplate)
    }

    void "ProductController aggregates a product and a review"() {
        given: "ProductService has a stubbed restTemplate for a productId"
        this.productService.restTemplate.getForObject(this.productService.reviewApiUrl, Review, [productId: PRODUCT_ID]) >> REVIEW
        this.productService.restTemplate.getForObject(ProductService.PRODUCT_API_URL, Product, [productId: PRODUCT_ID]) >> PRODUCT

        when: "ProductController aggregation endpoint is called with the productId"
        AggregateResponse aggregateResponse = this.productController.findProductById(PRODUCT_ID)

        then: "Aggregated review is stubbed one"
        REVIEW.averageReviewScore == aggregateResponse.averageReviewScore
        REVIEW.numberOfReviews == aggregateResponse.numberOfReviews

        then: "Aggregated product is stubbed one"
        PRODUCT.id == aggregateResponse.id
        PRODUCT.name == aggregateResponse.name
    }

    void "ProductService responds 404 when asked to find unexisting product"() {
        given: "ProductService has a stubbed restTemplate that does not find a review for a product"
        this.productService.restTemplate.getForObject(this.productService.reviewApiUrl, Review, [productId: PRODUCT_ID]) >> REVIEW
        this.productService.restTemplate.getForObject(ProductService.PRODUCT_API_URL, Product, [productId: PRODUCT_ID]) >> {
            throw new HttpClientErrorException(HTTP_STATUS)
        }

        when: "ProductController is asked to find the product"
        this.productController.findProductById(PRODUCT_ID)

        then: "ProductController responds 404"
        ResponseStatusException responseStatusException = thrown()
        HTTP_STATUS == responseStatusException.status
    }

    void "ProductService responds 404 when asked to find a product without review"() {
        given: "ProductService has a stubbed restTemplate that does not find a review for a product"
        this.productService.restTemplate.getForObject(this.productService.reviewApiUrl, Review, [productId: PRODUCT_ID]) >> {
            throw new HttpClientErrorException(HTTP_STATUS)
        }
        this.productService.restTemplate.getForObject(ProductService.PRODUCT_API_URL, Product, [productId: PRODUCT_ID]) >> PRODUCT

        when: "ProductController is asked to find the product"
        this.productController.findProductById(PRODUCT_ID)

        then: "ProductController responds 404"
        ResponseStatusException responseStatusException = thrown()
        HTTP_STATUS == responseStatusException.status
    }

    void "ProductService responds 500 when external product service responds 500"() {
        given: "ProductService has a stubbed restTemplate that responds 500 for a product"
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        this.productService.restTemplate.getForObject(this.productService.reviewApiUrl, Review, [productId: PRODUCT_ID]) >> REVIEW
        this.productService.restTemplate.getForObject(ProductService.PRODUCT_API_URL, Product, [productId: PRODUCT_ID]) >> {
            throw new HttpServerErrorException(httpStatus)
        }

        when: "ProductController is asked to find the product"
        this.productController.findProductById(PRODUCT_ID)

        then: "ProductController responds 500"
        ResponseStatusException responseStatusException = thrown()
        httpStatus == responseStatusException.status
    }

    void "ProductService responds 500 when review service responds 500"() {
        given: "ProductService has a stubbed restTemplate that responds 500 for a product"
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        this.productService.restTemplate.getForObject(this.productService.reviewApiUrl, Review, [productId: PRODUCT_ID]) >> {
            throw new HttpServerErrorException(httpStatus)
        }
        this.productService.restTemplate.getForObject(ProductService.PRODUCT_API_URL, Product, [productId: PRODUCT_ID]) >> PRODUCT

        when: "ProductController is asked to find the product"
        this.productController.findProductById(PRODUCT_ID)

        then: "ProductController responds 500"
        ResponseStatusException responseStatusException = thrown()
        httpStatus == responseStatusException.status
    }
}
