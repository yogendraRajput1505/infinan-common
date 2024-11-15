package com.infinan.common.service;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.LTPQuote;

@Service
public class GetLivePriceUsingWebSocket {
	protected static final Logger LOGGER = LoggerFactory.getLogger(GetLivePriceUsingWebSocket.class);
	
	@Autowired
	M_HistoricalDataService historicalDataService;
	
	public HistoricalData loadLivePriceData(KiteConnect kiteConnect, String tradingSymbol) {
		try {
			HistoricalData historicalData = null;
			while(ObjectUtils.isEmpty(historicalData)) {
				double liveDataViaWebSocket = getLiveDataViaWebSocket(kiteConnect,tradingSymbol);
				historicalData = convertWebSocketDatatoHistoricalData(liveDataViaWebSocket);
			}
			return historicalData;
		} catch (Exception e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return null;
	}
	
	public double getLiveDataViaWebSocket(KiteConnect kiteConnect, String tradingSymbol) {
		try {
			String[] str = {historicalDataService.getInstrumentToken(tradingSymbol)};			
			Optional<LTPQuote> findFirst = kiteConnect.getLTP(str).values().stream().findFirst();
			if(findFirst.isPresent()){
				return findFirst.get().lastPrice;
			}
		}catch (KiteException  e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
			LOGGER.info("Retrying...");
			if("Too many requests".equalsIgnoreCase(e.message)) {
				//Used this sleep so that 2 instances can use websocket. Remove it if required
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					LOGGER.error("An error occured : "+e.getMessage());
					LOGGER.error("Stack trace: ", e);
				}
				return getLiveDataViaWebSocket(kiteConnect,tradingSymbol);
			}
			e.printStackTrace();
		} catch (JSONException | IOException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		} 
		return 0;
	}
	
	public HistoricalData convertWebSocketDatatoHistoricalData(double value) {
		HistoricalData historicalData = new HistoricalData();
		historicalData.open = value;
		historicalData.high = value;
		historicalData.low = value;
		historicalData.close = value;
		historicalData.timeStamp = LastSecondCandleData.getCurrentDateAndTimeInKiteFormat();
		return historicalData;
	}
}
