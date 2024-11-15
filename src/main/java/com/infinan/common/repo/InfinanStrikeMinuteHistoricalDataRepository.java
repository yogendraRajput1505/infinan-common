package com.infinan.common.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infinan.common.entity.TInfinanStrikeMinuteHistoricalData;

public interface InfinanStrikeMinuteHistoricalDataRepository extends JpaRepository<TInfinanStrikeMinuteHistoricalData, String>{
	@Query("SELECT sd FROM TInfinanStrikeMinuteHistoricalData sd WHERE sd.timeStamp LIKE CONCAT(:timestamp, '%') AND sd.tradingSymbol LIKE CONCAT(:tradingSymbol, '%') ORDER BY sd.timeStamp")
    List<TInfinanStrikeMinuteHistoricalData> findByTimestampAndTradingSynmbol(String timestamp, String tradingSymbol);
	
	@Query("SELECT sd FROM TInfinanStrikeMinuteHistoricalData sd WHERE sd.timeStamp BETWEEN :startTimestamp AND :endTimestamp AND sd.tradingSymbol LIKE CONCAT(:symbol, '%') ORDER BY sd.timeStamp")
    List<TInfinanStrikeMinuteHistoricalData> findByTimestampBetweenAndTradingSymbol(String startTimestamp, String endTimestamp, String symbol);
	
}
