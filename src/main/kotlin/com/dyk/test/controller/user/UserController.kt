package com.dyk.test.controller.user

import com.dyk.test.domain.user.User
import com.dyk.test.dto.LoginRequestDto
import com.dyk.test.dto.UserProfileUpdateDto
import com.dyk.test.service.user.UserService


import com.dyk.test.dto.UserRegistrationDto
import com.dyk.test.config.security.jwt.JwtAuth
import com.dyk.test.config.security.jwt.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.BindingResult
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.security.sasl.AuthenticationException

@RestController
@RequestMapping("/api/user")
class UserController @Autowired constructor(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {
    @JwtAuth
    @GetMapping("/profile")
    fun getUserProfile(): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val user = userService.getUserProfile(username)
        return ResponseEntity.ok(user)
    }

    @JwtAuth
    @PutMapping("/profile")
    fun updateUserProfile(@RequestBody @Valid updateDto: UserProfileUpdateDto): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        try {
            val updatedUser = userService.updateUserProfile(username, updateDto.email, updateDto.nickname)
            return ResponseEntity.ok(mapOf("updatedUser" to updatedUser, "message" to "Profile updated successfully"))
        } catch (e: IllegalStateException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Invalid update")))
        }
    }

    @JwtAuth
    @DeleteMapping("/delete")
    fun deleteUser(@RequestParam username: String): ResponseEntity<String> {
         try {
            userService.deleteUser(username)
            return ResponseEntity.ok("Delete successfully")
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Invalid username provided")
        }
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any?> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.allErrors)
        }

        return try {
            val authenticatedUser = userService.authenticateUser(loginRequest.id, loginRequest.password)
            val jwtToken = jwtUtil.generateToken(authenticatedUser.username)
            val headers = org.springframework.http.HttpHeaders().apply {
                add("Authorization", "Bearer $jwtToken")
            }
            ResponseEntity.ok()
                .headers(headers)
                .body(mapOf("token" to jwtToken, "user" to authenticatedUser))

        } catch (e: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to (e.message ?: "Invalid credentials")))
        }
    }

    @PostMapping("/register")
    fun registerUser(
        @RequestBody @Valid userDto: UserRegistrationDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.allErrors)
        }
        return try {
            userService.registerUser(userDto.toEntity())
            ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }

    private fun UserRegistrationDto.toEntity() = User(
        id = this.id,
        password = this.password,
        email = this.email,
        username = this.username,
        nickname = this.nickname
    )

}