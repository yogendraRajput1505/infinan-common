package com.infinan.common.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.zerodhatech.models.HistoricalData;

@Component
@Service
public class Indicators {
	
	/**
	 * Formula - EMA	{Close - EMA(previous day)} x multiplier + EMA(previous day).
	 * @param historicalData - Data of candle
	 * @param period - Number of candles for which we are calculating the average
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Indicators.class);
	public double eMovingAverage(double closePrice, double multiplier, double previousEMA) {
		return 	(closePrice-previousEMA)*multiplier+previousEMA;	
	}
	
	/**
	 * @param period - Number of candles for which we are calculating the average
	 */
	public double calculateMultipler(int period) {
		return (double) 2/(period+1);
	}
	
	/**
	 * 
	 * @param count - number of candle processed
	 * @param laneEMA = Previous EMA Value
	 * @param laneMultipler = Multipler which is used for calculating EMA 
	 * @param lane - Value of Lane (Number of candles for which we are calculating the average i.e. - 6,12,21 etc)
	 * @param historicalData - Data of candle
	 * @return
	 */
	public double getEmaValue(int count, double laneEMA, double laneMultipler, int lane, HistoricalData historicalData) {
		if(count<lane) {
			laneEMA = laneEMA+historicalData.close;
		}
		else if (count==lane){
			laneEMA = laneEMA/lane;
		}else {
			laneEMA = eMovingAverage(historicalData.close, laneMultipler, laneEMA);
		}
		return roundToNearestMultiple(laneEMA,0.05);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
	}	

	public double roundToNearestMultiple(double value, double multiple) {
        double quotient = value / multiple;
        long roundedQuotient = Math.round(quotient);
        return roundedQuotient * multiple;
    }
	
	public double getEmaValue(List<HistoricalData> values, double laneMultipler, int lane) {
		double ema = values.get(values.size()-1).close;
		try {
			for(int i= values.size()-lane; i<values.size();i++) {
				ema = getEmaValue(lane+1, ema, laneMultipler, lane, values.get(i));
			}
		} catch (Exception e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return ema;
	}

	public double getEmaValueNew(List<HistoricalData> values, double laneMultipler, int lane) {
		double sma=0;
		try {
			for(int i= values.size()-lane; i<values.size()-1;i++) {
				sma += values.get(i).close;
			}
			sma = sma/(lane-1);
			return eMovingAverage(values.get(values.size()-1).close, laneMultipler, sma);
		} catch (Exception e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return 0;
	}
	
	public double getEmaValueNew2(List<HistoricalData> values, double laneMultipler, int lane) {
		double ema=0;
		try {
			for(int i= values.size()-lane; i<values.size()-1;i++) {
				ema = eMovingAverage(values.get(values.size()-1).close, laneMultipler, ema);
			}
			return eMovingAverage(values.get(values.size()-1).close, laneMultipler, ema);
		} catch (Exception e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return 0;
	}
	
}
