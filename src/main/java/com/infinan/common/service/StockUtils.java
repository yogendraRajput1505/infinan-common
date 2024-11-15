package com.infinan.common.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.infinan.common.repo.StockListRepository;
import com.infinan.common.utils.PropertyReaderCommon;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Instrument;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Quote;

@Component
public class StockUtils {
	
	@Autowired
	StockListRepository stockListRepository;
	
	@Autowired
	LoginToKite kite;
	
	// Try to manage Property Reader, Check can we fetch app.prop of this project
	@Autowired
	PropertyReaderCommon propertyReaderCommon;
	protected static final Logger LOGGER = LoggerFactory.getLogger("DateTime");
	
	private static final String BANKNIFTY = "BANKNIFTY";
	private static final String NIFTY50 = "NIFTY";
	private static final String FINNIFTY = "FINNIFTY";
	private static final String MIDCPNIFTY = "MIDCPNIFTY";
	private static final String SENSEX = "SENSEX";
	
	public static int findCE(Double value,int jump) {
		int rem = (int) (value%50); 
		int gap = 50;
		if (jump>1)
			gap = 50*jump;
		return (int) (value+gap-rem);
	}
	
	public static int findPE(Double value,int jump) {
		int rem = (int) (value%50); 
		int gap = 0;
		if (jump>0)
			gap = 50*jump;
		return (int) (value-rem-gap);
	}
	
	public static double getCandleLength(HistoricalData historicalData) {
		return Math.abs(historicalData.close-historicalData.open);		
	}
	
	public static double getCandlesSizeRatio(HistoricalData baseCandle, HistoricalData compareWith) {
		return getCandleLength(compareWith)/getCandleLength(baseCandle);
	}
	
	//We should need only one method. no two methods are required like isGreenCandle() or isRedCandle()
	public static boolean isGreenCandle(HistoricalData currentCandle){
		return currentCandle.open<currentCandle.close;
	}
	
	public static boolean isRedCandle(HistoricalData currentCandle) {
		return currentCandle.open>currentCandle.close;
	}
		
	public boolean isCurrentCandleBelowThen50Per(HistoricalData currentCandle, HistoricalData previousCandle){
		double _50Per = (previousCandle.open+previousCandle.close)/2;
		//for green Candle
		if(currentCandle.close>currentCandle.open && currentCandle.open<_50Per)
				return true;
		//for red Candle
		else if(currentCandle.close<currentCandle.open && currentCandle.close<_50Per)
				return true;
		
		return false;
	}
	
	public boolean isCurrentCandleAboveThen50Per(HistoricalData currentCandle, HistoricalData previousCandle){
		
		double _50Per = (previousCandle.open+previousCandle.close)/2;
		//for green Candle
		if(currentCandle.close>currentCandle.open && currentCandle.close>_50Per)
				return true;
		//for red Candle
		else if(currentCandle.close<currentCandle.open && currentCandle.open>_50Per)
				return true;
		
		return false;
	}
	
	/** Get instruments for the desired exchange.
	 * @return */
    public static List<Instrument> getInstrumentsForExchange(KiteConnect kiteConnect, String exchange){
        List<Instrument> instruments = null;
		try {
			instruments = kiteConnect.getInstruments(exchange);
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
        return instruments;
    }
    /**
     * Need to Refractor this method to handle other instrument tokens
     * @return
     */
	public static Map<String,String> getIndexInstrumentToken() {
		Map<String,String> instrumentTokens=new HashMap<>();
		instrumentTokens.put("NIFTY50","256265");
		instrumentTokens.put("FINNIFTY","257801");
		instrumentTokens.put("BANKNIFTY","260105");
		instrumentTokens.put("MIDCPNIFTY","288009");
		instrumentTokens.put("SENSEX","265");
		return instrumentTokens;
	}
	
	/**
	 * 
	 * @param scrip
	 * @param exchange
	 * @return
	 */
	public String getInstrumentToken(String scrip, String exchange) {		
		try {
			Quote nseQuote = getQuote(exchange,scrip).get(exchange+":"+scrip);
			if(ObjectUtils.isEmpty(nseQuote) )
				throw new NullPointerException();
			return nseQuote.instrumentToken+"";	
		}catch(NullPointerException e) {
			LOGGER.error("An error occured : Error Occured while getting Instrument Token for scrip : "+scrip);
			LOGGER.error("Stack trace: ", e);
		}
		return "";
	}
    
	public Map<String, Quote> getQuote(String exchange, String instrument) {	
		String[] instruments = {exchange+":"+instrument};
		// This If Block Should execute for only NSE instruments
//		if(instrument.startsWith("NIFTY") && !instrument.equals("NIFTY 50")) {
//			List<String> allByIndices = stockListRepository.getAllNameByIndices(instrument);
//			instruments = allByIndices.stream().toArray(String[] ::new);
//			for(int i=0;i<instruments.length;i++ ) {
//				instruments[i] = exchange+":"+instruments[i];
//			}
//		}
        Map<String, Quote> quotes = null;
		try {
			quotes = kite.getKiteConnect().getQuote(instruments);
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return quotes;
	}
    
    public HistoricalData getHistoricalData(String intrumentToken, String from, String to,String timeFrame) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate =  new Date();
        Date toDate = new Date();
        try {
            fromDate = formatter.parse(from);
            toDate = formatter.parse(to);
        }catch (ParseException e) {
        	LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
        }
        HistoricalData historicalData = null;
		try {
			historicalData = kite.getKiteConnect().getHistoricalData(fromDate, toDate, intrumentToken, timeFrame, false, true);
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
//		candles.analyzeSingleCandlePattern(historicalData);
        return historicalData;
	}
    
    public void placeOrderToKite(int quantity, String orderType, String tradingsymbol, String product, String exchange, String transactionType,
			String validity, double price, double triggerPrice, String tag) {
		OrderParams orderParams = new OrderParams();
		orderParams.quantity = quantity;
		orderParams.orderType = orderType;
		orderParams.tradingsymbol = tradingsymbol;
		orderParams.product = product;
		orderParams.exchange = exchange;
		orderParams.transactionType = transactionType;
		orderParams.validity = validity;
		orderParams.price = price;
		orderParams.triggerPrice = triggerPrice;
		//orderParams.tag = tag; //tag is optional and it cannot be more than 8 characters and only alphanumeric is allowed
		
	//	Order order = null;
		try {
			kite.getKiteConnect().placeOrder(orderParams, Constants.VARIETY_REGULAR);
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		// System.out.println(order.orderId);
	}
    
	public double getTargetPrice(double buyPrice, double targetPercentage) {
		return roundToNearestMultiple(targetPercentage/100*buyPrice, 0.05);
	}
	
	public double roundToNearestMultiple(double value, double multiple) {
		DecimalFormat df = new DecimalFormat("#.##");
        double quotient = value / multiple;
        long roundedQuotient = Math.round(quotient);
        return Double.parseDouble(df.format(roundedQuotient * multiple));
    }
  
	
	public String getCurrentDay(){
		Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // DAY_OF_WEEK returns values from 1 (Sunday) to 7 (Saturday)
        String dayOfWeekString;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayOfWeekString = "Sunday";
                break;
            case Calendar.MONDAY:
                dayOfWeekString = "Monday";
                break;
            case Calendar.TUESDAY:
                dayOfWeekString = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeekString = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayOfWeekString = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayOfWeekString = "Friday";
                break;
            case Calendar.SATURDAY:
                dayOfWeekString = "Saturday";
                break;
            default:
                dayOfWeekString = "Invalid day of the week";
        }
        return dayOfWeekString;
	}
	/**
	 * 
	 * @param dateStr1
	 * @param dateStr2
	 * @param formatOne - format of dateStr1 (2023-12-04T10:27:00+0530 or Tue Dec 04 10:28:00 IST 2023)
	 * @param formatTwo - format of dateStr2 (2023-12-04T10:27:00+0530 or Tue Dec 04 10:28:00 IST 2023)
	 * @return difference in milli second
	 */
	  public static long parseDateAndGetDifference(String dateStr1, String dateStr2, String formatOne, String formatTwo) {
		  SimpleDateFormat dateFormatOne = new SimpleDateFormat(formatOne);  
		  SimpleDateFormat dateFormatTwo = new SimpleDateFormat(formatTwo);
	        
	        try {
	            return calculateDifference(dateFormatOne.parse(dateStr1), dateFormatTwo.parse(dateStr2));
	        } catch (ParseException e) {
	        	LOGGER.error("An error occured : "+e.getMessage());
				LOGGER.error("Stack trace: ", e);
	        }
	        return 0;
	    }

	  public static long calculateDifference(Date date1, Date date2){
		  return (date2.getTime() - date1.getTime())/1000; 
	  }
	  
	  
			
	public int getQuantity(String tradingSymbol, int lot) {
		if(tradingSymbol.startsWith(FINNIFTY)) {
			return lot*propertyReaderCommon.getFinniftyLotSize();
		}else if(tradingSymbol.startsWith(BANKNIFTY)) {
			return lot*propertyReaderCommon.getBankniftyLotSize();
		}else if(tradingSymbol.startsWith(NIFTY50)){
			return lot*propertyReaderCommon.getNiftyLotSize();
		}else if(tradingSymbol.startsWith(MIDCPNIFTY)){
			return lot*propertyReaderCommon.getMidniftyLotSize();
		}else if(tradingSymbol.startsWith(SENSEX)){
			return lot*propertyReaderCommon.getSensexLotSize();
		}
		return 0;
	}
		
	public int getLotSize(String tradingSymbol) {
		if(tradingSymbol.startsWith(FINNIFTY)) {
			return propertyReaderCommon.getFinniftyLotSize();
		}else if(tradingSymbol.startsWith(BANKNIFTY)) {
			return propertyReaderCommon.getBankniftyLotSize();
		}else if(tradingSymbol.startsWith(NIFTY50)){
			return propertyReaderCommon.getNiftyLotSize();
		}else if(tradingSymbol.startsWith(MIDCPNIFTY)){
			return propertyReaderCommon.getMidniftyLotSize();
		}else if(tradingSymbol.startsWith(SENSEX)){
			return propertyReaderCommon.getSensexLotSize();
		}
		return 1;//Controller should no come till here for Good Case
	}
	
	public int calculateLot(double buyPrice,double tradeAmount, String tradingSymbol) {
		return (int) (tradeAmount/(getLotSize(tradingSymbol)*buyPrice));
	}
	
	public static boolean checkCongicutiveRedcandle(List<HistoricalData> pastCandle, int noOfCongicutiveCandles) {
		//Buy order place hone wali candle se just previous wali candle
		//HistoricalData candle = pastCandle.get(pastCandle.size()-2);
		int nonCongicutiveRedCandleCount=0;
		for(int i=0;i<pastCandle.size()-2;i++) {
			if(StockUtils.isRedCandle(pastCandle.get(i))) {
				nonCongicutiveRedCandleCount++;
			}else {
				nonCongicutiveRedCandleCount=0;
			}
			if(nonCongicutiveRedCandleCount>=noOfCongicutiveCandles) {
				nonCongicutiveRedCandleCount=0;		
//				LOGGER.info("checkFourCongicutiveRedcandle() RETURN TRUE");
				return true;
			}
		}
		return false;
	}
		
}
