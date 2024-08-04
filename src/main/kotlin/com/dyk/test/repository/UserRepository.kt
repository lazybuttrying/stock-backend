package com.dyk.test.repository


import com.dyk.test.domain.user.User
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findById(id: String): User?
    fun findByUsername(username: String): User?

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.username = :username")
    fun softDeleteByUsername(username: String)

}
