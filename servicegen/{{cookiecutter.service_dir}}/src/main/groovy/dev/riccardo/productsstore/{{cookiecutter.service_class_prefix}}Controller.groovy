package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@SpringBootApplication
@RestController
class {{cookiecutter.service_class_prefix}}Controller {
    @Autowired
    private {{cookiecutter.service_class_prefix}}Service {{cookiecutter.service_name}}Service

    static void main(String[] args) {
        SpringApplication.run({{cookiecutter.service_class_prefix}}Controller, args)
    }

    // Your ReST endpoints go here
    @GetMapping("/hello")
    String hello() {
        this.{{cookiecutter.service_name}}Service.hello()
    }
}


@Service
class {{cookiecutter.service_class_prefix}}Service {
    private Logger logger = LoggerFactory.getLogger({{cookiecutter.service_class_prefix}}Service)
    // Your business logic goes here
    String hello() {
        logger.info "Saying hello to a user"
        "Hello from {{cookiecutter.service_name}} service!\n{{cookiecutter.description}}"
    }
}


