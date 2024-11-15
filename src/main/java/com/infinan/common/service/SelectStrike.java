package com.infinan.common.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class SelectStrike {
	
	private static final String BANKNIFTY = "BANKNIFTY";
	private static final String NIFTY50 = "NIFTY";
	private static final String FINNIFTY = "FINNIFTY";
	private static final String MIDCPNIFTY = "MIDCPNIFTY";
	private static final String SENSEX = "SENSEX";
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectStrike.class);
	// -->
	private int getATMPrice(String instrumentTokens, double price) {
		if(instrumentTokens.startsWith(BANKNIFTY) || instrumentTokens.startsWith(SENSEX)) {
			return getRoundedPrice(price,100);
		}else if(instrumentTokens.startsWith(MIDCPNIFTY)) {
			return getRoundedPrice(price,25);
		}
		return getRoundedPrice(price,50);
	}
	
	private int getOTMPriceCE(String instrumentTokens, double price, int jump) {
		if(instrumentTokens.startsWith(BANKNIFTY) || instrumentTokens.startsWith(SENSEX)) {
			 return getATMPrice(instrumentTokens,price)+100*jump;
		}else if(instrumentTokens.startsWith(MIDCPNIFTY)) {
			return getATMPrice(instrumentTokens,price)+25*jump;
		}
		return getATMPrice(instrumentTokens,price)+50*jump;	
	}
		
	/**
	 * 
	 * @param instrumentTokens
	 * @param price
	 * @param jump - How much away from 1st ITM with respect to option chart. i.e ->  jump = 1 means immediate ITM, jump =2 means 2nd ITM strike
	 * @return
	 */
	private int getITMPriceCE(String instrumentTokens, double price, int jump) {
		if(instrumentTokens.startsWith(BANKNIFTY) || instrumentTokens.startsWith(SENSEX)) {
			 return getATMPrice(instrumentTokens,price)-100*jump;
		}else if(instrumentTokens.startsWith(MIDCPNIFTY)) {
			return getATMPrice(instrumentTokens,price)-25*jump;
		}
		return getATMPrice(instrumentTokens,price)-50*jump;
	}
	
	private int getOTMPricePE(String instrumentTokens, double price, int jump) {
		return getITMPriceCE(instrumentTokens, price, jump);
	}
	
	private int getITMPricePE(String instrumentTokens, double price, int jump) {
		return getOTMPriceCE(instrumentTokens, price, jump);
	}
	
	// -->
	private int getRoundedPrice(double price, int multipler){
		double value = price / multipler;
	    double roundedValue = Math.round(value); 
	    return (int)roundedValue * multipler;
	}
	
	/**
	 * 
	 * BANKNIFTY 23OCT 44500 PE - Format For Month's last Expiary (23 represent year)
	 * BANKNIFTY 23O11 44500 CE - Format For Other Expiary
	 * @return
	 */
//	private String getExpiaryDate(String instrumentTokens, LocalDate ld) {
//		if(ld==null)
//			ld = LocalDate.now();	
//		
//		//Temporarly Added Dinnifty Block here because expiary set to Monday because there si holiday on Tuesday
//		if(instrumentTokens.startsWith(MIDCPNIFTY)){
//			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));					
//		}
//		else if(instrumentTokens.startsWith(FINNIFTY)) {
//			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
//		}
//		else if(instrumentTokens.startsWith(BANKNIFTY)) {
//			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));			
//		}
//		//Adding sensex because expiary was changed - remove sensex condition
//		else if(instrumentTokens.startsWith(NIFTY50)){
//			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));					
//		}
//		else if(instrumentTokens.startsWith(SENSEX)){
//			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));					
//		}
//		
//        int daysLeftInMonth = ld.lengthOfMonth() - ld.getDayOfMonth();
//		
//        if(daysLeftInMonth <7) {
//			return ld.getYear()%2000+ld.getMonth().toString().substring(0, 3);			
//		}
//        
//        String days = ld.getDayOfMonth()+"";
//        if(ld.getDayOfMonth() <10) {
//        	days = "0"+ld.getDayOfMonth();
//        }
//       
//        return (ld.getYear()-2000)+""+ld.getMonthValue()+days;
////        In 2023 below code was working fine but not working for 2024
////        return (ld.getYear()-2000)+ld.getMonth().toString().substring(0, 1)+days;
//        
//	}
	
	/**
	 * 
	 * BANKNIFTY 23OCT 44500 PE - Format For Month's last Expiary (23 represent year)
	 * BANKNIFTY 23O11 44500 CE - Format For Other Expiary
	 * @return
	 */
	private String getExpiaryDate(String instrumentTokens, LocalDate ld) {
		if(ld==null)
			ld = LocalDate.now();	
		
		//Temporarly Added Dinnifty Block here because expiary set to Monday because there si holiday on Tuesday
		if(instrumentTokens.startsWith(MIDCPNIFTY)){
			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));					
		}
		else if(instrumentTokens.startsWith(FINNIFTY)) {
			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
		}
		else if(instrumentTokens.startsWith(BANKNIFTY)) {
			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));			
		}
		//Adding sensex because expiary was changed - remove sensex condition
		else if(instrumentTokens.startsWith(NIFTY50)){
			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));					
		}
		else if(instrumentTokens.startsWith(SENSEX)){
			ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));					
		}
		
        int daysLeftInMonth = ld.lengthOfMonth() - ld.getDayOfMonth();
		
        if(daysLeftInMonth <7) {
			return ld.getYear()%2000+ld.getMonth().toString().substring(0, 3);			
		}
        
        String days = ld.getDayOfMonth()+"";
        if(ld.getDayOfMonth() <10) {
        	days = "0"+ld.getDayOfMonth();
        }
       
        return (ld.getYear()-2000)+""+ld.getMonthValue()+days;
//        In 2023 below code was working fine but not working for 2024
//        return (ld.getYear()-2000)+ld.getMonth().toString().substring(0, 1)+days;
        
	}
	
	
	private String getAtmCeStrike(String instrumentTokens,double price, LocalDate ld, int jump) {
		return instrumentTokens+getExpiaryDate(instrumentTokens,ld)+getATMPrice(instrumentTokens, price)+"CE";
	}
	
	private String getAtmPeStrike(String instrumentTokens,double price, LocalDate ld, int jump) {
		return instrumentTokens+getExpiaryDate(instrumentTokens,ld)+getATMPrice(instrumentTokens, price)+"PE";
	}
	
	private String getOtmCeStrike(String instrumentTokens,double price, LocalDate ld, int jump) {
		return instrumentTokens+getExpiaryDate(instrumentTokens,ld)+getOTMPriceCE(instrumentTokens, price, jump)+"CE";
	}
	
	private String getOtmPeStrike(String instrumentTokens,double price, LocalDate ld, int jump) {
		return instrumentTokens+getExpiaryDate(instrumentTokens,ld)+getOTMPricePE(instrumentTokens, price, jump)+"PE";
	}
	
	private String getItmCeStrike(String instrumentTokens,double price, LocalDate ld, int jump) {
		return instrumentTokens+getExpiaryDate(instrumentTokens,ld)+getITMPriceCE(instrumentTokens, price, jump)+"CE";
	}
	
	private String getItmPeStrike(String instrumentTokens,double price, LocalDate ld, int jump) {
		return instrumentTokens+getExpiaryDate(instrumentTokens,ld)+getITMPricePE(instrumentTokens, price, jump)+"PE";
	}
	
//	public String setTradingSymbol(String index, double price, boolean isPE, String strike, LocalDate ld) {
//		LOGGER.info("index : "+index+", price : "+price+", isPE : "+isPE+", strike : "+", LocalDate : "+ld);
//		if("AUTO".equalsIgnoreCase(strike)) {
//			return chooseAutomaticTradingSymbol(index, price, isPE, ld);
//		}else {
//			return  chooseManuallyTradingSymbol(index,price,isPE,strike,ld);
//		}			
//	}
	
	public String setTradingSymbol(String index, double price, boolean isPE, String strike, LocalDate ld, LocalTime time,int jump) {
		//LOGGER.info("index : "+index+", price : "+price+", isPE : "+isPE+", strike : "+strike+", LocalDate : "+ld+", Time : "+time);
		if("AUTO".equalsIgnoreCase(strike)) {
			return chooseAutomaticTradingSymbol(index, price, isPE, ld, time,jump);
		}else {
			return  chooseManuallyTradingSymbol(index,price,isPE,strike,ld,jump);
		}			
	}
	
	private String chooseManuallyTradingSymbol(String index, double price, boolean isPE,String strike, LocalDate ld, int jump) {
		if(isPE) {
			switch(strike) {
				case "ITM" :
					return getItmPeStrike(index,price,ld,jump);
				case "ATM" : 			
					return getAtmPeStrike(index,price,ld,jump);
				case "OTM" : 			
					return getOtmPeStrike(index,price,ld,jump);
				default:
					return getAtmPeStrike(index,price,ld,jump);
			}
		}
		else {
			switch(strike) {
				case "ITM" : 			
					return getItmCeStrike(index,price,ld,jump);
				case "ATM" : 			
					return getAtmCeStrike(index,price,ld,jump);
				case "OTM" : 			
					return getOtmCeStrike(index,price,ld,jump);
				default:
					return getAtmPeStrike(index,price,ld,jump);
			}
		}
	}
	
//	private String chooseAutomaticTradingSymbol(String index, double price, boolean isPE, LocalDate ld, int jump) {
//		LocalTime currentTime = LocalTime.now();
//		return chooseAutomaticTradingSymbol(index, price, isPE, ld, currentTime,jump);
//	}	
	
	private String chooseAutomaticTradingSymbol(String index, double price, boolean isPE, LocalDate ld, LocalTime currentTime, int jump) {
		if(currentTime == null)
			currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();
        if(isPE) {
            if (currentHour > 9 && currentHour<=14) {
            	return getOtmPeStrike(index,price,ld,jump);
            } else{
            	return getAtmPeStrike(index,price,ld,jump);
            } 
        }else {
        	if (currentHour > 9 && currentHour<=14) {
            	return getOtmCeStrike(index,price,ld,jump);
            } else {
            	return getAtmCeStrike(index,price,ld,jump);
            }
        }
	}	
}
