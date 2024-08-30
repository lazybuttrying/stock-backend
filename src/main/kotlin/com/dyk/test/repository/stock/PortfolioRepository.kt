package com.dyk.test.repository.stock

import com.dyk.test.domain.stock.Portfolio
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PortfolioRepository : JpaRepository<Portfolio, UUID>