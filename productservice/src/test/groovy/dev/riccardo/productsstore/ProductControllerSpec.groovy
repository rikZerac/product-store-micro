package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest
class ProductControllerSpec extends Specification {
    @Autowired
    private ProductController productController
    @Autowired
    private ProductService productService

    void "Application has ProductController"() {
        expect:
        this.productController
    }

    void "ProductService aggregates a product and a review"() {
        given: "ProductService has a stubbed restTemplate for a productId"
        Long productId = 13
        Review review = new Review(productId: productId, averageReviewScore: 5.0, numberOfReviews: 10)
        Product product = new Product(id: productId, name: "Pop corn")
        this.productService.restTemplate = Mock(RestTemplate)
        this.productService.restTemplate.getForObject(ProductService.REVIEW_API_URL, Review, [productId: productId]) >> review
        this.productService.restTemplate.getForObject(ProductService.PRODUCT_API_URL, Product, [productId: productId]) >> product

        when: "ProductController aggregation endpoint is called with the productId"
        AggregateResponse aggregateResponse = this.productController.findProductById(productId)

        then: "Aggregated review is stubbed one"
        review.averageReviewScore == aggregateResponse.averageReviewScore
        review.numberOfReviews == aggregateResponse.numberOfReviews

        then: "Aggregated product is stubbed one"
        product.id == aggregateResponse.id
        product.name == aggregateResponse.name
    }
}
