package com.dyk.test.domain.stock

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "news")
data class News(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    val uuid: UUID? = null,
    val title: String,
    val publisher: String,
    val link: String,
    val providerPublishTime: LocalDateTime,
    val type: String,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    val thumbnail: Thumbnail? = null,

    @ManyToMany(mappedBy = "news", fetch = FetchType.LAZY)
    val stocks: List<Stock> = emptyList(),
)

@Entity
data class Thumbnail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "thumbnail_resolutions",
        joinColumns = [JoinColumn(name = "thumbnail_id")],
//        uniqueConstraints = [UniqueConstraint(columnNames = ["url"])]  // 유니크 제약 조건 설정
    )
    val resolutions: List<Resolution> = listOf()
)

@Embeddable
data class Resolution(
    val url: String,
    val width: Int,
    val height: Int,
    val tag: String
)