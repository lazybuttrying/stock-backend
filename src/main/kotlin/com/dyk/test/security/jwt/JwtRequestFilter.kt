package com.dyk.test.security.jwt

import com.dyk.test.service.user.CustomUserDetailsService

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@Component
class JwtRequestFilter @Autowired constructor(
    private val userDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader("Authorization")
        var username: String? = null
        var jwt: String? = null

        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7)
            try {
                username = jwtUtil.getUsername(jwt)
            } catch (e: Exception) {
                println("JWT token is invalid: ${e.message}")
                println(e)
            }
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = this.userDetailsService.loadUserByUsername(username)

            if (jwtUtil.validateToken(jwt!!)) {
                val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
            }
        }
        chain.doFilter(request, response)
    }
}
