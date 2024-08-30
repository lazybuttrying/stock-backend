package com.dyk.test.service.table


import com.dyk.test.repository.HyperTableRepository
import com.dyk.test.repository.stock.StockRepository
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

@Service
class DatabaseService(
    private val myTableRepository: HyperTableRepository,
    private val stockRepository: StockRepository,
    private val apiService: ApiService
) {

    @Value("\${hypertable.created}")
    private lateinit var hypertableCreated: String

    @Transactional
    fun createHypertableIfNotExists() {
        if (hypertableCreated.toBoolean().not()) {
            // Hypertable 생성
            myTableRepository.createHypertable("price", "time")

            // 생성 후 환경 설정 업데이트 (필요시 외부 파일이나 DB에 저장)
            val properties = Properties()
            properties.setProperty("hypertable.created", "true")
            // 저장 로직 필요 (예: 파일이나 DB에 업데이트)
        }
    }

    @PostConstruct
    fun init() {
        val resource = ClassPathResource("data/stock.csv")
        val reader = BufferedReader(InputStreamReader(resource.inputStream))

        val stocks = reader.lineSequence().toList()
        apiService.fetchInfo(stocks)
        apiService.fetchPrice(stocks)


        println("Database has been initialized with initial data.")
    }
}