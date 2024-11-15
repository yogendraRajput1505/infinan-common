package com.infinan.common.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zerodhatech.models.HistoricalData;

@Service
public class StrategySupportingAttributes<E> {
	private double stoplossPercentage;
	private double crossoverTime;
	private double volumeForCondition;	
	private List <E> tradesList = new ArrayList<>();
	private double slowLaneMA=0;
	private double fastLaneMA=0;
	private double tradeBuyPrice=0;
	private HistoricalData previousCandle;
	private boolean localTestingMode = false;
	private boolean liveTestingMode = false;
		
	public boolean isLocalTestingMode() {
		return localTestingMode;
	}

	public void setLocalTestingMode(boolean localTestingMode) {
		this.localTestingMode = localTestingMode;
	}

	public boolean isLiveTestingMode() {
		return liveTestingMode;
	}

	public void setLiveTestingMode(boolean liveTestingMode) {
		this.liveTestingMode = liveTestingMode;
	}

	public double getSlowLaneMA() {
		return slowLaneMA;
	}

	public double getFastLaneMA() {
		return fastLaneMA;
	}

	public void setSlowLaneMA(double slowLaneMA) {
		this.slowLaneMA = slowLaneMA;
	}
	
	public void setFastLaneMA(double d) {
		this.fastLaneMA = d;
	}
	
	public double getStoplossPercentage() {
		return stoplossPercentage;
	}

	public double getCrossoverTime() {
		return crossoverTime;
	}
	
	public double getVolumeForCondition() {
		return volumeForCondition;
	}
	
	public void setStoplossPercentage(double stopLossPercentage) {
	    this.stoplossPercentage = stopLossPercentage;
	}
	
	public void setCrossoverTime(double crossoverTime) {
	    this.crossoverTime = crossoverTime;
	}
	
	public void setVolumeForCondition(double volumeForCondition) {
	    this.volumeForCondition = volumeForCondition;
	}
	
	public List<E> getTradesList() {
		return tradesList;
	}

	public void setTradesList(List<E> tradesList) {
		this.tradesList = tradesList;
	}

	public double getTradeBuyPrice() {
		return tradeBuyPrice;
	}

	public void setTradeBuyPrice(double tradeBuyPrice) {
		this.tradeBuyPrice = tradeBuyPrice;
	}

	public HistoricalData getPreviousCandle() {
		return previousCandle;
	}

	public void setPreviousCandle(HistoricalData previousCandle) {
		this.previousCandle = previousCandle;
	}

	@Override
	public String toString() {
		return "StrategySupportingAttributes [stoplossPercentage=" + stoplossPercentage + ", crossoverTime="
				+ crossoverTime + ", volumeForCondition=" + volumeForCondition + ", tradesList=" + tradesList
				+ ", slowLaneMA=" + slowLaneMA + ", fastLaneMA=" + fastLaneMA + ", tradeBuyPrice=" + tradeBuyPrice
				+ ", previousCandle=" + previousCandle + "]";
	}
	
}
