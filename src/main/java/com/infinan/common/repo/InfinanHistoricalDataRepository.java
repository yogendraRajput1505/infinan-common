package com.infinan.common.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infinan.common.entity.TInfinanHistoricalData;

public interface InfinanHistoricalDataRepository extends JpaRepository<TInfinanHistoricalData, String>{
	@Query("SELECT sd FROM TInfinanHistoricalData sd WHERE sd.timeStamp BETWEEN :startTimestamp AND :endTimestamp AND sd.tradingSymbol LIKE CONCAT(:symbol, '%') ORDER BY sd.timeStamp")
    List<TInfinanHistoricalData> findByTimestampBetween(String startTimestamp, String endTimestamp, String symbol);
	
	@Query("SELECT DISTINCT thd.tradingSymbol FROM TInfinanHistoricalData thd WHERE thd.timeStamp LIKE %:timeStampPattern%")
    List<String> findDistinctTradingSymbolsByTimeStamp(String timeStampPattern);
	
	@Query("SELECT DISTINCT thd.tradingSymbol FROM TInfinanHistoricalData thd WHERE thd.timeStamp LIKE %:timeStampPattern% AND thd.tradingSymbol LIKE :symbol% ")
    List<String> findDistinctTradingSymbolsByTimeStampAndIndex(String timeStampPattern, String symbol);
}
