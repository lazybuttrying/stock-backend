package com.dyk.test.service.user

import com.dyk.test.domain.user.User
import com.dyk.test.repository.user.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.willDoNothing
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import javax.security.sasl.AuthenticationException


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    @Order(1)
    fun `registerUser should save user with encrypted password`() {
        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            nickname = "test",
            username = "test"
        )
        given(userRepository.findByEmail(user.email)).willReturn(null)
        given(userRepository.findById(user.id)).willReturn(null)
        given(passwordEncoder.encode(user.password)).willReturn("encryptedPassword")
        given(userRepository.save(any(User::class.java))).willAnswer { it.getArgument(0) }

        // when
        val savedUser = userService.registerUser(user)

        // then
        assertNotNull(savedUser)
        assertEquals("encryptedPassword", savedUser.password)
    }

    @Test
    @Order(2)
    fun `registerUser should throw IllegalArgumentException when email already exists`() {

        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            nickname = "test",
            username = "test"
        )
        given(userRepository.findByEmail(user.email)).willReturn(user)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.registerUser(user)
        }
        assertEquals("Email already in use", exception.message)
    }

    @Test
    @Order(2)
    fun `registerUser should throw IllegalArgumentException when ID already exists`() {
        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            nickname = "test",
            username = "test"
        )

        given(userRepository.findByEmail(user.email)).willReturn(null)
        given(userRepository.findById(user.id)).willReturn(user)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.registerUser(user)
        }
        assertEquals("ID already in use", exception.message)
    }

    @Test
    @Order(2)
    fun `authenticateUser should return user when credentials are valid`() {
        // given
        val id = "123"
        val password = "password"

        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            nickname = "test",
            username = "test"
        )

        given(userRepository.findById(id)).willReturn(user)
        given(passwordEncoder.matches(password, user.password)).willReturn(true)

        // when
        val authenticatedUser = userService.authenticateUser(id, password)

        // then
        assertNotNull(authenticatedUser)
        assertEquals(user.id, authenticatedUser.id)
    }

    @Test
    @Order(2)
    fun `authenticateUser should throw AuthenticationException when user not found`() {
        // given
        val wrongId = "456"
        val password = "password"

        given(userRepository.findById(wrongId)).willReturn(null)

        // when & then
        val exception = assertThrows<AuthenticationException> {
            userService.authenticateUser(wrongId, password)
        }
        assertEquals("User not found", exception.message)
    }

    @Test
    @Order(2)
    fun `authenticateUser should throw AuthenticationException when credentials are invalid`() {
        // given
        val id = "123"
        val wrongPassword = "password123"

        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            nickname = "test",
            username = "test"
        )

        given(userRepository.findById(id)).willReturn(user)
        given(passwordEncoder.matches(wrongPassword, user.password)).willReturn(false)

        // when & then
        val exception = assertThrows<AuthenticationException> {
            userService.authenticateUser(id, wrongPassword)
        }
        assertEquals("Invalid credentials", exception.message)
    }


    @Test
    @Order(2)
    fun `getUserProfile should return user when username exists`() {
        // given
        val username = "test"

        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            nickname = "test",
            username = "test"
        )

        given(userRepository.findByUsername(username)).willReturn(user)

        // when
        val foundUser = userService.getUserProfile(username)

        // then
        assertNotNull(foundUser)
        assertEquals(user.username, foundUser.username)
    }

    @Test
    @Order(2)
    fun `getUserProfile should throw IllegalArgumentException when username does not exist`() {
        // given
        val wrongUsername = "nonexistent"
        given(userRepository.findByUsername(wrongUsername)).willReturn(null)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.getUserProfile(wrongUsername)
        }
        assertEquals("User not found with username: $wrongUsername", exception.message)
    }

    @Test
    @Order(2)
    fun `updateUserProfile should throw IllegalStateException when no changes detected`() {
        // given
        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            username = "test",
            nickname = "test"
        )

        given(userRepository.findByUsername(user.username)).willReturn(user)

        // when & then
        val exception = assertThrows<IllegalStateException> {
            userService.updateUserProfile(
                user.username, user.email, user.nickname)
        }
        assertEquals("No changes detected", exception.message)
    }

    @Test
    @Order(2)
    fun `updateUserProfile should throw IllegalArgumentException when user not found`() {
        // given
        val wrongUsername = "nonexistent"
        val newEmail = "new@example.com"
        val newNickname = "newNick"
        given(userRepository.findByUsername(wrongUsername)).willReturn(null)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.updateUserProfile(wrongUsername, newEmail, newNickname)
        }
        assertEquals("User not found with username: $wrongUsername", exception.message)
    }

    @Test
    @Order(3)
    fun `updateUserProfile should update email and nickname`() {
        // given
        val newEmail = "new@example.com"
        val newNickname = "newNick"

        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            username = "test",
            nickname = "test"
        )
        given(userRepository.findByUsername(user.username)).willReturn(user)
        given(userRepository.save(any(User::class.java))).willAnswer { it.getArgument(0) }

        // when
        val updatedUser = userService.updateUserProfile(user.username, newEmail, newNickname)

        // then
        assertNotNull(updatedUser)
        assertEquals(newEmail, updatedUser.email)
        assertEquals(newNickname, updatedUser.nickname)
    }

    @Test
    @Order(4)
    fun `deleteUser should throw IllegalArgumentException when user not found`() {
        // given
        val wrongUsername = "nonexistent"
        given(userRepository.findByUsername(wrongUsername)).willReturn(null)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.deleteUser(wrongUsername)
        }
        assertEquals("User not found with username: $wrongUsername", exception.message)
    }

    @Test
    @Order(5)
    fun `deleteUser should delete user when username exists`() {
        // given
        val username = "test"
        val user = User(
            id = "123",
            email = "test@example.com",
            password = "password",
            username = "test",
            nickname = "test"
        )

        given(userRepository.findByUsername(username)).willReturn(user)
        willDoNothing().given(userRepository).softDeleteByUsername(username)

        // when
        assertDoesNotThrow {
            userService.deleteUser(username)
        }

        // then
        // Verify the softDeleteByUsername method was called
        verify(userRepository).softDeleteByUsername(username)
    }

}