package com.infinan.common.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;

@Service
public class M_HistoricalDataService {
	
	@Autowired
	LoginToKite kite;
	
	@Autowired
	StockUtils stockUtils; 
	
	protected static final Logger LOGGER = LoggerFactory.getLogger("M_HistoricalDataService");
	
	/** Get Historical Data*/
	public HistoricalData getHistoricalData(
			String fromDate,String fromTime,
			String toDate,String toTime,
			String interval,
			String tradingSymbol) throws KiteException {
		KiteConnect kiteConnect=kite.getKiteConnect();
		HistoricalData historicalDataList = null;
		try {
            String fromDateNTime=fromDate+" "+fromTime;
            String toDateNTime=toDate+" "+toTime;
            
            tradingSymbol=tradingSymbol.toUpperCase();
            String instrumentToken = getInstrumentToken(tradingSymbol);  
            // Convert date strings to Date objects
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fromDateObj = dateFormat.parse(fromDateNTime);
            Date toDateObj = dateFormat.parse(toDateNTime);
            // Fetch historical data
            //If we are fetching data from 14:52:33 to 14:58:33 then  we are getting data for only 14:52:33 to 14:57:33 - 
            //why we are not getting data for last candle - this is useless maybe
         //   System.out.println("fromDateObj : "+fromDateObj+" , toDateObj : "+toDateObj);
            historicalDataList = kiteConnect.getHistoricalData(fromDateObj,toDateObj,instrumentToken,interval,false,true);
        } catch (Exception e) {
        	LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
        }
		return historicalDataList;
	}

	public String getInstrumentToken(String tradingSymbol) {
		String instrumentToken = null;            
		if(StockUtils.getIndexInstrumentToken().containsKey(tradingSymbol)) {
			instrumentToken = StockUtils.getIndexInstrumentToken().get(tradingSymbol);
		}else {
			if(tradingSymbol.startsWith("SENSEX")) {
		    	instrumentToken = stockUtils.getInstrumentToken(tradingSymbol, "BFO");            				
			}else {
				instrumentToken = stockUtils.getInstrumentToken(tradingSymbol, "NFO");
			}
		}
		return instrumentToken;
	}
	
	/** Hammer*/
	public List<HistoricalData> getHammers(
			String fromDate,String fromTime,
			String toDate,String toTime,
			String interval,
			String tradingSymbol) throws KiteException {
		
//		M_HistoricalDataService hds=new M_HistoricalDataService();
		M_HistoricalDataService hds=this;
		HistoricalData historicalData= hds.getHistoricalData(fromDate,fromTime,toDate,toTime,interval,tradingSymbol);
		
		List<HistoricalData> historicalDatalist= historicalData.dataArrayList;
		
		List<HistoricalData> newData=new ArrayList<>();
		for(HistoricalData hd:historicalDatalist) {
			
			double open=hd.open;
			double close=hd.close;
			double low=hd.low;
			double high=hd.high;
			/*
			 * boolean hammer=hds.isHammer(open,close,high,low); if(hammer) {
			 * newData.add(hd); }
			 */
			double bodySize = Math.abs(open - close);
		    double lowerWick = Math.min(open, close) - low;
		    double upperWick = high - Math.max(open, close);

		    // Define thresholds for body and shadow ratios
		     
		    if(lowerWick>2*bodySize && upperWick<bodySize) {
		    	newData.add(hd);                                                                
		    }
		}
		
		return newData;
		
	}
	
	/** Place BUY Order for Options */
	public String placeBuyOptionOrder(String tradingSymbol,int quantity) {
		
		KiteConnect kiteConnect = kite.getKiteConnect();
		// orderParams.tradingsymbol = "NIFTY24AUG19500CE
		try {
            OrderParams orderParams = new OrderParams();
            orderParams.quantity = quantity;
            orderParams.orderType = Constants.ORDER_TYPE_MARKET;
            orderParams.tradingsymbol = tradingSymbol;
            orderParams.product = Constants.PRODUCT_MIS;
            orderParams.exchange = Constants.EXCHANGE_NFO;
            orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
            orderParams.validity = Constants.VALIDITY_DAY;
            orderParams.tag = "myTag";

            Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
            return "Order ID: " + order.orderId;
        } catch (KiteException | IOException e) {
        	LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
            return "Failed to place order";
        }
    }
	
	/** Place SELL Order for Options */
	public String placeSellOptionOrder(String tradingSymbol,int quantity) {
		
		KiteConnect kiteConnect = kite.getKiteConnect();
		// orderParams.tradingsymbol = "NIFTY24AUG19500CE
		try {
            OrderParams orderParams = new OrderParams();
            orderParams.quantity = quantity;
            orderParams.orderType = Constants.ORDER_TYPE_MARKET;
            orderParams.tradingsymbol = tradingSymbol;
            orderParams.product = Constants.PRODUCT_MIS;
            orderParams.exchange = Constants.EXCHANGE_NFO;
            orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;
            orderParams.validity = Constants.VALIDITY_DAY;
            orderParams.tag = "myTag";

            Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
            return "Order ID: " + order.orderId;
        } catch (KiteException | IOException e) {
        	LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
            return "Failed to place order";
        }
    }
	
	
}
