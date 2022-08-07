package dev.riccardo.productsstore

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class HttpSecurityConfiguration {
    @Bean
    SecurityFilterChain configureHttpSecurity(HttpSecurity httpSecurity) {
        httpSecurity.tap {
            authorizeHttpRequests { AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry matcherRegistry ->
                matcherRegistry.tap {
                    antMatchers(HttpMethod.GET, "/**").permitAll()
                    antMatchers("/h2-console/**/*").permitAll()
                    anyRequest().authenticated()
                }
            }
            httpBasic(Customizer.withDefaults())
            // Development/Demo setup:
            // h2 web console can be rendered in an HTML document frame
            headers().frameOptions().disable()
            // and after login authenticated write endpoints are accessible through swagger ui
            csrf().disable()
        }.build()
    }

    @Bean
    UserDetailsService userDetailsService() {
        new InMemoryUserDetailsManager(
            User.withUsername("lee").tap {
                password("{bcrypt}\$2a\$10\$jxe54MqVmLTiGK3Q0IL12eHM4m0Bhc6NLSm30IhxfGzJ.QvF/GZly")
                roles("ADMIN")
            }.build()
        )
    }
}
