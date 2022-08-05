package dev.riccardo.productsstore

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WelcomeController {
    @GetMapping(["/", "/home", "/welcome"])
    String goToWelcomePage() {
        "welcome"
    }
}
