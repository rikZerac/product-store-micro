package dev.riccardo.productsstore

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SpringFoxConfiguration {
    @Bean
    Docket provide() {
       new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(
                new ApiInfo(
                "Review service ReST API",
                "ReST API of microservice to manage products reviews",
                "1.0",
                null,
                new Contact(
                    "Riccardo Caretti",
                    "https://github.com/rikZerac/product-store-micro",
                    "postarcr@gmail.com"
                ),
                null,
                null,
                [])
            )
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
    }
}
