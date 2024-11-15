package com.infinan.common.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.infinan.common.entity.TIndexHistoricalData;
import com.infinan.common.entity.TInfinanHistoricalData;
import com.infinan.common.entity.TInfinanStrikeMinuteHistoricalData;
import com.infinan.common.repo.InfinanHistoricalDataRepository;
import com.infinan.common.utils.PropertyReaderCommon;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
@Service
public class HistoricalDataUtils {
	private static Calendar calendar = Calendar.getInstance();
	private static final int HISTORICAL_DATA_LIST_BUFFER_COUNT = 120; //120 second of data will save at once
	
	@Autowired
	LoginToKite kite;

	@Autowired
	SelectStrike selectStrike;
	
	@Autowired
	M_HistoricalDataService m_HistoricalDataService;
	
	@Autowired
	InfinanHistoricalDataRepository infinanHistoricalDataRepository;
	
	@Autowired
	PropertyReaderCommon propertyReaderCommon;
	
	protected static final Logger LOGGER = LoggerFactory.getLogger("HistoricalDataUtils");
	
	//set a catch block for getting data when too many request error came
	public List<HistoricalData> getHistoricalData(String instrument, Date fromDate, Date toDate)
		        throws JSONException, KiteException, Exception {
		try {
			if("NIFTY".equals(instrument)) {
				instrument = "NIFTY50";
			}
			List<HistoricalData> dataArrayList = kite.getKiteConnect().getHistoricalData(fromDate, toDate, m_HistoricalDataService.getInstrumentToken(instrument), "minute", false, true).dataArrayList;
//			System.out.println("getHistoricalData() 2 invoked");
//			if(ObjectUtils.isEmpty(dataArrayList)) {
//				System.out.println("Data is empty for "+instrument+", from : "+fromDate+", "+toDate);
//			}
			return dataArrayList;
		}catch(SocketTimeoutException e) {
			LOGGER.error("An error occurred: " + e.getMessage());
			LOGGER.error("Stack trace: ", e);
			return getHistoricalData(instrument, fromDate, toDate);
		}
	}

	public void addDataToList(List<TInfinanHistoricalData> tInfinanHistoricalDataList, List<HistoricalData> historicalDataList, String scrip) {
	    if (!historicalDataList.isEmpty()) {
	        tInfinanHistoricalDataList.add(convertHistoricalDataToInfinanHistoricalData(historicalDataList.get(0), scrip));
	    }
	}

	public void handleDataBuffer(List<TInfinanHistoricalData> tInfinanHistoricalDataList) {
	    if (tInfinanHistoricalDataList.size() > HISTORICAL_DATA_LIST_BUFFER_COUNT) {
	        saveDataToDatabase(tInfinanHistoricalDataList);
	    }
	}

	public void handleFinalSetOfData(List<TInfinanHistoricalData> tInfinanHistoricalDataList) {
	    saveDataToDatabase(tInfinanHistoricalDataList);
	}


	public synchronized void saveDataToDatabase(List<TInfinanHistoricalData> tInfinanHistoricalDataList) {
	    if (!tInfinanHistoricalDataList.isEmpty()) {
	        infinanHistoricalDataRepository.saveAll(tInfinanHistoricalDataList);
	        tInfinanHistoricalDataList.clear();
	    }
	}
	
	public List<String> getCePeScrips(String index, double price, LocalTime localTime) {
		List<String> list = new ArrayList<>();
		list.add(selectStrike.setTradingSymbol(index, price, false, "AUTO", propertyReaderCommon.getDateToUse(), localTime,1));
		list.add(selectStrike.setTradingSymbol(index, price, true, "AUTO", propertyReaderCommon.getDateToUse(), localTime,1));
		
		return list;
	}

	public Date getDateTime(int hour, int minute){
		String date = propertyReaderCommon.getDateToUse().toString().split("-")[2];//Taking Date		
		calendar.set(Calendar.DATE, Integer.parseInt(date));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
	}
	
	public Date getDateTime(int hour, int minute, int second){
		String date = LocalDate.now().toString().split("-")[2];	
		calendar.set(Calendar.DATE, Integer.parseInt(date));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
	}
	
	public static List<String> getListForDay() {
		Map<DayOfWeek, List<String>> dayOfWeekToListMap = new HashMap<>();
        dayOfWeekToListMap.put(DayOfWeek.MONDAY, Arrays.asList("MIDCPNIFTY", "FINNIFTY", "BANKNIFTY"));
        dayOfWeekToListMap.put(DayOfWeek.TUESDAY, Arrays.asList("FINNIFTY", "BANKNIFTY", "NIFTY"));
        dayOfWeekToListMap.put(DayOfWeek.WEDNESDAY, Arrays.asList("BANKNIFTY", "NIFTY", "SENSEX"));
        dayOfWeekToListMap.put(DayOfWeek.THURSDAY, Arrays.asList("NIFTY", "SENSEX", "BANKNIFTY"));
        dayOfWeekToListMap.put(DayOfWeek.FRIDAY, Arrays.asList( "SENSEX","MIDCPNIFTY", "FINNIFTY"));
//      return dayOfWeekToListMap.getOrDefault(LocalDate.now().getDayOfWeek(), Collections.emptyList());
        return dayOfWeekToListMap.getOrDefault(54, Arrays.asList("MIDCPNIFTY"));
    }
	
	//it can cause issue when we try to save data for 2 or more index on same day
	public List<String> getIndexForDay() {
		Map<DayOfWeek, List<String>> dayOfWeekToListMap = new HashMap<>();
        dayOfWeekToListMap.put(DayOfWeek.MONDAY, Arrays.asList("MIDCPNIFTY"));
        dayOfWeekToListMap.put(DayOfWeek.TUESDAY, Arrays.asList("FINNIFTY"));
        dayOfWeekToListMap.put(DayOfWeek.WEDNESDAY, Arrays.asList("BANKNIFTY"));
        dayOfWeekToListMap.put(DayOfWeek.THURSDAY, Arrays.asList("NIFTY"));
        dayOfWeekToListMap.put(DayOfWeek.FRIDAY, Arrays.asList( "SENSEX"));
        return dayOfWeekToListMap.getOrDefault(LocalDate.now().getDayOfWeek(), Collections.emptyList());
    }
	
	public TInfinanHistoricalData convertHistoricalDataToInfinanHistoricalData(HistoricalData historicalData, String tradingSymbol) {
		TInfinanHistoricalData data = new TInfinanHistoricalData();
		if(historicalData==null)
			return null;
		data.setAtm_otm_itm("AUTO");
		data.setClose(historicalData.close);
		data.setHigh(historicalData.high);
		data.setId(historicalData.timeStamp+"_"+tradingSymbol);
		data.setLow(historicalData.low);
		data.setOi(historicalData.oi);
		data.setOpen(historicalData.open);
		data.setTimeStamp(historicalData.timeStamp);
		data.setTradingSymbol(tradingSymbol);
		data.setVolume(historicalData.volume);
		return data;
	}
	
	public TInfinanStrikeMinuteHistoricalData convertHistoricalDataToInfinanMinuteHistoricalData(HistoricalData historicalData, String tradingSymbol) {
		TInfinanStrikeMinuteHistoricalData data = new TInfinanStrikeMinuteHistoricalData();
		if(historicalData==null)
			return null;
		data.setAtm_otm_itm("AUTO");
		data.setClose(historicalData.close);
		data.setHigh(historicalData.high);
		data.setId(historicalData.timeStamp+"_"+tradingSymbol);
		data.setLow(historicalData.low);
		data.setOi(historicalData.oi);
		data.setOpen(historicalData.open);
		data.setTimeStamp(historicalData.timeStamp);
		data.setTradingSymbol(tradingSymbol);
		data.setVolume(historicalData.volume);
		return data;
	}
	
	public List<HistoricalData> convertInfinanMinuteHistoricalDataListToHistoricalDataList(List<TInfinanStrikeMinuteHistoricalData> historicalDataList, String tradingSymbol) {
		return historicalDataList.stream()
			.map(historicalData -> convertInfinanMinuteHistoricalDataToHistoricalData(historicalData))
			.collect(Collectors.toList());		
	}
	
	public TIndexHistoricalData convertHistoricalDataToIndexHistoricalData(HistoricalData historicalData, String tradingSymbol) {
		TIndexHistoricalData data = new TIndexHistoricalData();
		if(historicalData==null)
			return null;
		data.setClose(historicalData.close);
		data.setHigh(historicalData.high);
		data.setId(historicalData.timeStamp+"_"+tradingSymbol);
		data.setLow(historicalData.low);
		data.setOpen(historicalData.open);
		data.setTimeStamp(historicalData.timeStamp);
		data.setTradingSymbol(tradingSymbol);
		return data;
	}
	
	public HistoricalData convertIndexHistoricalDataToHistoricalData(TIndexHistoricalData data) {
		HistoricalData historicalData = new HistoricalData();
		if(data==null)
			return null;
		historicalData.close = data.getClose();
		historicalData.high = data.getHigh();
		historicalData.low = data.getLow();
		historicalData.open = data.getOpen();
		historicalData.timeStamp = data.getTimeStamp();
		return historicalData;
	}
	
	public HistoricalData convertInfinanMinuteHistoricalDataToHistoricalData(TInfinanStrikeMinuteHistoricalData data) {
		HistoricalData historicalData = new HistoricalData();
		if(data==null)
			return null;
		historicalData.close = data.getClose();
		historicalData.high = data.getHigh();
		historicalData.low = data.getLow();
		historicalData.open = data.getOpen();
		historicalData.timeStamp = data.getTimeStamp();
		historicalData.volume = data.getVolume();
		historicalData.oi = data.getOi();
		return historicalData;
	}
	
	public HistoricalData convertInfinanHistoricalDataToHistoricalData(TInfinanHistoricalData data) {
		HistoricalData historicalData = new HistoricalData();
		if(data==null)
			return null;
		historicalData.close = data.getClose();
		historicalData.high = data.getHigh();
		historicalData.low = data.getLow();
		historicalData.open = data.getOpen();
		historicalData.timeStamp = data.getTimeStamp();
		historicalData.volume = data.getVolume();
		historicalData.oi = data.getOi();
		return historicalData;
	}
	
	public boolean isProcessed(String index, String time) {
		String date = propertyReaderCommon.getDateToUse().toString().split("-")[2];//Taking Date	
		String key = index + "_" + time+"_"+date;
        try {
            ClassPathResource resource = new ClassPathResource("files/processed_data.txt");
            File file = resource.getFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals(key)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
        	LOGGER.error("An error occurred: " + e.getMessage());
			LOGGER.error("Stack trace: ", e);
//            e.printStackTrace();
        }
        return false;
	}
	
	public synchronized void markAsProcessed(String value, String time) {
		String date = propertyReaderCommon.getDateToUse().toString().split("-")[2];//Taking Date	
	    String key = value + "_" + time+"_"+date;
	    try {
            ClassPathResource resource = new ClassPathResource("files/processed_data.txt");
            File file = resource.getFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(key + "\n");
            }
        } catch (IOException e) {
        	LOGGER.error("An error occurred: " + e.getMessage());
			LOGGER.error("Stack trace: ", e);
//            e.printStackTrace();
        }
	}
	
}
