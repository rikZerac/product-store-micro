package dev.riccardo.productsstore

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class ProductService {
	static void main(String[] args) {
		SpringApplication.run(ProductService, args)
	}

    @GetMapping("/product/{productId}")
    Product getProduct(@PathVariable String productId) {
        new Product(productId: productId)
    }
}

class Product {
    String productId
}
