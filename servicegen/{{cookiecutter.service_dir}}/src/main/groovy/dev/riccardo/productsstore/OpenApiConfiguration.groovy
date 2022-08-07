package dev.riccardo.productsstore

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.customizers.OpenApiCustomiser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Value('${microservice.version}')
    String _version

    @Bean
    OpenApiCustomiser provide() {
        return { OpenAPI openApi ->
            openApi.info(new Info().tap {
                description("{{cookiecutter.description}}")
                version(_version)
                title("{{cookiecutter.service_class_prefix}} service")
                contact(
                    new Contact().tap {
                        name("{{cookiecutter.author}}")
                        email("{{cookiecutter.author_email}}")
                        url("https://github.com/rikZerac/product-store-micro")
                    }
                )
            })
        }
    }
}
