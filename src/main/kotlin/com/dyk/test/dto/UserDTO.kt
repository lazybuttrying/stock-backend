package com.dyk.test.dto


import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserRegistrationDto(
    @field:NotBlank @field:Size(min = 3, max = 50) val id: String = "",
    @field:NotBlank @field:Size(min = 6) val password: String = "",
    @field:NotBlank @field:Email val email: String = "",
    @field:NotBlank val username: String = "",
    @field:NotBlank val nickname: String = "",
)

data class LoginRequestDto(
    @field:NotBlank @field:Size(min = 3, max = 50) val id: String,
    @field:NotBlank @field:Size(min = 6) val password: String
)

data class UserProfileUpdateDto(
    @field:NotBlank(message = "Email is mandatory")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Nickname is mandatory")
    val nickname: String
)