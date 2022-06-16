package me.ssttkkl.sharenote.configuration

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import me.ssttkkl.sharenote.service.AuthService
import me.ssttkkl.sharenote.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey


@EnableWebSecurity
class SecurityConfiguration(val userService: UserService) : WebSecurityConfigurerAdapter() {

    companion object {
        private val ignore = arrayOf(
            "/auth/**",
            "/users",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs",
        )
    }

    @Value("\${auth.jwt.public-key}")
    lateinit var key: RSAPublicKey

    @Value("\${auth.jwt.private-key}")
    lateinit var priv: RSAPrivateKey

    override fun configure(http: HttpSecurity) {
        http
            // Enable CORS and disable CSRF
            .cors { }
            .csrf { it.disable() }
            .sessionManagement {
                // Set session management to stateless
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                // Set unauthorized requests exception handler
                it.authenticationEntryPoint(BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(BearerTokenAccessDeniedHandler())
            }
            .authorizeRequests {
                // Our public endpoints
                it.antMatchers(*ignore).permitAll()
                // Our private endpoints
                it.anyRequest().authenticated()
            }
            // Set up oauth2 resource server
            .httpBasic(Customizer.withDefaults())
            .oauth2ResourceServer()
            .jwt()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService)
    }

    // Expose authentication manager bean
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    // Used by JwtAuthenticationProvider to generate JWT tokens
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(key).build()
    }

    // Used by JwtAuthenticationProvider to decode and validate JWT tokens
    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk = RSAKey.Builder(key).privateKey(priv).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    // Set password encoding schema
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // Used by spring security if CORS is enabled.
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().apply {
            allowCredentials = true
            addAllowedOrigin("*")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    // Extract authorities from the roles claim
    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        return JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(JwtGrantedAuthoritiesConverter().apply {
                setAuthoritiesClaimName("roles")
                setAuthorityPrefix("ROLE_")
            })
        }
    }
}