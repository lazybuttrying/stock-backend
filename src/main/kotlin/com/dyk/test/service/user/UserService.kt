package com.dyk.test.service.user


import com.dyk.test.domain.user.User
import com.dyk.test.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.security.sasl.AuthenticationException

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun registerUser(user: User): User {
        if (userRepository.findByEmail(user.email) != null) {
            throw IllegalArgumentException("Email already in use")
        }
        if (userRepository.findById(user.id) != null) {
            throw IllegalArgumentException("ID already in use")
        }
        val encryptedUser = user.copy(password = passwordEncoder.encode(user.password))
        return userRepository.save(encryptedUser)
    }

    fun authenticateUser(id: String, password: String): User {
        val user = userRepository.findById(id)
            ?: throw AuthenticationException("User not found")

        if (!passwordEncoder.matches(password, user.password)) {
            throw AuthenticationException("Invalid credentials")
        }

        return user
    }

    fun getUserProfile(username: String): User {
        return userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found with username: $username")
    }

    @Transactional
    fun updateUserProfile(username: String, email: String, nickname: String): User {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found with username: $username")

        var updated = false

        if (user.email != email) {
            user.email = email
            updated = true
        }

        if (user.nickname != nickname) {
            user.nickname = nickname
            updated = true
        }

        return if (updated) {
            userRepository.save(user)
        } else {
            throw IllegalStateException("No changes detected")
        }
    }

    @Transactional
    fun deleteUser(username: String) {
        userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found with username: $username")

        userRepository.softDeleteByUsername(username)
    }
}
