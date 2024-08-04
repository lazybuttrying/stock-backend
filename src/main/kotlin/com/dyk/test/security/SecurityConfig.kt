package com.dyk.test.security


import com.dyk.test.security.jwt.JwtRequestFilter
import com.dyk.test.service.user.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig (
    private val jwtRequestFilter: JwtRequestFilter,
    private val customUserDetailsService: CustomUserDetailsService
){

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return customUserDetailsService
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/api/users/delete", permitAll)
                authorize("/api/users/profile", permitAll)
                authorize("/api/users/login", permitAll)
                authorize("/api/users/register", permitAll)
                authorize(anyRequest, authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtRequestFilter)
            formLogin { disable() } // 로그인 창 비활성화
            httpBasic { disable() } // 로그인 인증창 비활성화
        }
        return http.build()
    }
}
