package com.infinan.common.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infinan.common.entity.TIndexHistoricalData;
import com.infinan.common.entity.TInfinanHistoricalData;
import com.infinan.common.entity.TInfinanStrikeMinuteHistoricalData;
import com.infinan.common.repo.IndexMinuteHistoricalDataRepository;
import com.infinan.common.repo.InfinanHistoricalDataRepository;
import com.infinan.common.repo.InfinanStrikeMinuteHistoricalDataRepository;
import com.zerodhatech.models.HistoricalData;

@Service
public class ReadInfinanHistoricalDataService {
	
	
	@Autowired
	InfinanHistoricalDataRepository infinanHistoricalDataRepository;
	
	@Autowired
	IndexMinuteHistoricalDataRepository indexMinuteHistoricalDataRepository;
	
	@Autowired
	InfinanStrikeMinuteHistoricalDataRepository infinanStrikeMinuteHistoricalDataRepository ;
	
	@Autowired
	HistoricalDataUtils historicalDataUtils;
	
	protected static final Logger LOGGER = LoggerFactory.getLogger("ReadInfinanHistoricalDataService");
	
	public List<TInfinanHistoricalData> infinanSecondHistoricalDataList(String index, LocalDate currentDate){
		String startTimestamp =currentDate.toString()+"T09:15:00+0530";
		String endTimestamp =currentDate.toString()+"T15:30:00+0530";
		return infinanHistoricalDataRepository.findByTimestampBetween(startTimestamp, endTimestamp, index);
	}
	
	public List<String> findDistinctTradingSymbolsByTimeStamp(String timeStamp){
		return infinanHistoricalDataRepository.findDistinctTradingSymbolsByTimeStamp(timeStamp);
	}
	
	public List<String> findDistinctTradingSymbolsByTimeStampAndIndex(String timeStamp, String index){
		return infinanHistoricalDataRepository.findDistinctTradingSymbolsByTimeStampAndIndex(timeStamp,index);
	}
	
	public Map<String, TInfinanHistoricalData> rearrangeInfinanSecondHistoricalDataList(String index, LocalDate currentDate) {
		 List<TInfinanHistoricalData> dataList = infinanSecondHistoricalDataList(index,currentDate);
		    Map<String, TInfinanHistoricalData> resultMap = new LinkedHashMap<>();
		    for (TInfinanHistoricalData data : dataList) {
		    	String key = data.getId();
		        resultMap.put(key, data);
		    }
		    return resultMap;
	}
	
	/**
	 * This method will return minute historical  data for current day
	 * @param index - For which index we want data
	 * @return 
	 */
	public List<TIndexHistoricalData> getIndexHistoricalData(String index, LocalDate currentDate) {
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = currentDate.toString();
		//String formattedDate = "2024-02-13";
		return  indexMinuteHistoricalDataRepository.findByTimestampAndTradingSynmbol(formattedDate, index);
		//System.out.println(formattedDate);
		//return findByTimestampAndTradingSynmbol;
	}
	/**
	 * 
	 * @param index - Will give data for all trading symbol of given index for given day
	 * @return
	 */
	public List<TInfinanStrikeMinuteHistoricalData> getStrikeMinuteHistoricalData(String index, LocalDate currentDate ){
		//LocalDate currentDate = LocalDate.now();
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = currentDate.toString();
		//String formattedDate = "2024-02-13";
		return infinanStrikeMinuteHistoricalDataRepository.findByTimestampAndTradingSynmbol(formattedDate, index);
		//System.out.println(formattedDate);
		//return findByTimestampAndTradingSynmbol;
	}
	
	
	/**
	 * 
	 * @param index - Will give data for matching trading symbol of given time and Date
	 * @return
	 */
	public List<TInfinanStrikeMinuteHistoricalData> getStrikeMinuteHistoricalData(String tradingSymbol, String fromDate, String toDate ){
		return infinanStrikeMinuteHistoricalDataRepository.findByTimestampBetweenAndTradingSymbol(fromDate, toDate, tradingSymbol);
//		return findByTimestampAndTradingSynmbol;
	}
	
	//Why do we need to take it in MAP? dataList is already sorted...we can use index for fetching value...let's see
	public Map<String, TIndexHistoricalData> rearrangeIndexHistoricalData(String index, LocalDate currentDate) {
		List<TIndexHistoricalData> dataList = null;
		if("NIFTY".equalsIgnoreCase(index)) {
		    dataList = getIndexHistoricalData(index+"50", currentDate);
		}else {
		    dataList = getIndexHistoricalData(index, currentDate);	
		}
	    Map<String, TIndexHistoricalData> resultMap = new LinkedHashMap<>();
	    for (TIndexHistoricalData data : dataList) {
//	        String key = data.getTimeStamp().substring(0, 16) + "_" + data.getTradingSymbol();
	    	String key = data.getId();
	        resultMap.put(key, data);
	    }
	    return resultMap;
	}
	
	// For this use of MAP is fine.. because with index we can't track the data if strike price changes
	public Map<String, TInfinanStrikeMinuteHistoricalData> rearrangeStrikeMinuteHistoricalData(String index, LocalDate currentDate) {
	    List<TInfinanStrikeMinuteHistoricalData> dataList = getStrikeMinuteHistoricalData(index,currentDate);
	    Map<String, TInfinanStrikeMinuteHistoricalData> resultMap = new LinkedHashMap<>();
	    for (TInfinanStrikeMinuteHistoricalData data : dataList) {
//	        String key = data.getTimeStamp().substring(0, 16) + "_" + data.getTradingSymbol();
	    	String key = data.getId();
	        resultMap.put(key, data);
	    }
	    return resultMap;
	}

	/**
	 * key format of map -> timestamp_tradingSymbol. ex -> "2024-02-13T09:15:00+0530_BANKNIFTY2421444800PE"
	 * @param noOfHistoricalCandle 
	 * @param tradingSymbol
	 * @param currentMinute
	 * @param strikeMinuteHistoricalData
	 * @return 
	 */
	public List<HistoricalData> getHistoricalCandle(int noOfHistoricalCandle, String tradingSymbol, String currentMinute, Map<String, TInfinanStrikeMinuteHistoricalData> strikeMinuteHistoricalData) {
		LocalDateTime dateTime = LocalDateTime.parse(currentMinute);
//		System.out.println(dateTime);
		List<HistoricalData> dataList = new ArrayList<>();
		if(dateTime.getHour()>=10 || (dateTime.getHour()==9 && dateTime.getMinute()>15+noOfHistoricalCandle)){
			for(int i=noOfHistoricalCandle-1;i>0;i--) {
				LocalDateTime minusMinutes = dateTime.minusMinutes(i).withSecond(0);
				try {
					TInfinanStrikeMinuteHistoricalData tInfinanStrikeMinuteHistoricalData = strikeMinuteHistoricalData.get(minusMinutes.toString()+":00+0530_"+tradingSymbol);
					dataList.add(historicalDataUtils.convertInfinanMinuteHistoricalDataToHistoricalData(tInfinanStrikeMinuteHistoricalData));
				}catch(Exception e) {
					LOGGER.error("An error occurred: " + e.getMessage());
					LOGGER.error("Stack trace: ", e);
					e.printStackTrace();
				}
			}
		}
			
		return dataList;
	}
	
	public HistoricalData loadCurrentStrikeSecondData(String tradingSymbol, String currentMinute, int currentSecond, Map<String, TInfinanHistoricalData> collectHistoricalData) {
		String dateTime = LocalDateTime.parse(currentMinute).withSecond(currentSecond).toString();
		return historicalDataUtils.convertInfinanHistoricalDataToHistoricalData(collectHistoricalData.get(dateTime+"+0530_"+tradingSymbol));
	}
}
