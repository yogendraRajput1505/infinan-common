package com.infinan.common.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infinan.common.entity.TIndexHistoricalData;

public interface IndexMinuteHistoricalDataRepository extends JpaRepository<TIndexHistoricalData, String>{
	@Query("SELECT sd FROM TIndexHistoricalData sd WHERE sd.timeStamp LIKE CONCAT(:timestamp, '%') AND sd.tradingSymbol = :tradingSymbol ORDER BY sd.timeStamp")
    List<TIndexHistoricalData> findByTimestampAndTradingSynmbol(String timestamp, String tradingSymbol);
}
