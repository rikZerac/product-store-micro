package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ProductServiceSpec extends Specification {
    @Autowired
    private ProductService productService

    def "Application has ProductService"() {
        expect:
        this.productService
    }
}
