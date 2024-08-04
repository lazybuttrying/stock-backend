package com.dyk.test.domain.user

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*

enum class Role {
    USER,
    ADMIN
}

@Entity
@Table(name = "`user`")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    val userId: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,

    @Column(unique = true, nullable = false)
    val id: String,

    @Column(nullable = false)
    val password: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    val username: String,

    @Column(unique = true, nullable = false)
    var nickname: String,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,

    val deletedAt: LocalDateTime? = null
)