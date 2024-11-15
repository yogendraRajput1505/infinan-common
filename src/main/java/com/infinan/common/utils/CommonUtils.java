package com.infinan.common.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.infinan.common.service.GetHistoricalData;
import com.zerodhatech.models.HistoricalData;

@Component
@Service
public class CommonUtils {
	@Autowired
	GetHistoricalData getHistoricalData;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
	/**
	 * 
	 * @param numberOne
	 * @param numberTwo
	 * @param lowerBound - Low of candle or Open of Green Candle or Close of Red Candle
	 * @param upperBound - High of Candle or Close of Green Candle or Open of Red Candle
	 * @return
	 */
	public static boolean isBothBetweenRange(double numberOne, double numberTwo, double lowerBound, double upperBound) {
		return (numberOne >= lowerBound && numberOne <= upperBound) && (numberTwo >= lowerBound && numberTwo <= upperBound);
    }
	
	public static boolean isOneBetweenRange(double numberOne, double numberTwo, double lowerBound, double upperBound) {
		return (numberOne >= lowerBound && numberOne <= upperBound) || (numberTwo >= lowerBound && numberTwo <= upperBound);
    }
	
	List<HistoricalData> historicalDataList =null;
	
	//Not Required We can comment it
	public List<HistoricalData> getHistoricalData(String scrip, String from, String to, String timeFrame) {
		if(historicalDataList == null) {
			historicalDataList = getHistoricalData.getHistoricalData(scrip,from,to,timeFrame);
		}
		return historicalDataList;
	}
	
	 public static double calculatePercentageOfRange(double low, double high, double percentage) {
        double range = high - low; 
        return low + (range * percentage / 100); 
    }
	 
	 public static OffsetDateTime getOffsetDateTime(String timeStamp) {
		 return OffsetDateTime.parse(timeStamp, formatter);
	 }
	 
	 public static String getOffsetDateTime(String inputDate, String inputFormat) {		 
		 DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat, Locale.ENGLISH);
		 LocalDateTime localDateTime = LocalDateTime.parse(inputDate, inputFormatter);
		 ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Kolkata")); // IST - Indian Standard Time
		 
		// Format output
	       return zonedDateTime.format(formatter);

	        // Output result
//	        System.out.println(outputDate);
//		 
//		 return zonedDateTime.toOffsetDateTime();
	 }
}
