package com.infinan.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Quote;

@Component
@Service
public class GetHistoricalData {
	@Autowired 
	StockUtils stockUtils;
	
	public List<HistoricalData> getHistoricalData(String scrip, String from, String to, String timeFrame){
		Map<String, Quote> nseQuote = stockUtils.getQuote("NSE",scrip);
		String instrumentToken = ""+nseQuote.get("NSE:"+scrip).instrumentToken;
		HistoricalData historicalData = stockUtils.getHistoricalData(instrumentToken, from, to, timeFrame);
		return historicalData.dataArrayList;
	}
}
