package dev.riccardo.productsstore

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.customizers.OpenApiCustomiser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Bean
    OpenApiCustomiser provide() {
        return { OpenAPI openApi ->
            openApi.info(new Info().tap {
                description("ReST API documentation of microservice to aggregate products and reviews")
                version("1.0.0-SNAPSHOT")
                title("Product service")
                contact(
                    new Contact().tap {
                        name("Riccardo Caretti")
                        email("postarcr@gmail.com")
                        url("https://github.com/rikZerac/product-store-micro")
                    }
                )
            })
        }
    }
}
