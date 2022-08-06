package dev.riccardo.productsstore

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class {{cookiecutter.service_class_prefix}}ControllerSpec extends Specification {

    @Autowired
    private {{cookiecutter.service_class_prefix}}Controller {{cookiecutter.service_name}}Controller

    void "Application has {{cookiecutter.service_class_prefix}}Controller"() {
        expect:
        this.{{cookiecutter.service_name}}Controller
    }

    void "{{cookiecutter.service_class_prefix}}Controller says hello"() {
        expect:
        "Hello from {{cookiecutter.service_name}} service!\n{{cookiecutter.description}}" == this.{{cookiecutter.service_name}}Controller.hello()
    }
}
