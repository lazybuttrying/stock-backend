package com.dyk.test.repository

import com.dyk.test.domain.stock.Price
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface HyperTableRepository : CrudRepository<Price, Long> {

    @Transactional
    @Query(value = "SELECT create_hypertable(:tableName, :timeColumnName)", nativeQuery = true)
    fun createHypertable(tableName: String, timeColumnName: String)
}