package com.infinan.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
@Entity
public class TIndexHistoricalData {
	/**
	 * Id will be unique combination of  - 
	 * timeStamp+tradingSymbol+5digit random number
	 */
	@Id
	private String id;
	private String timeStamp;
	private String tradingSymbol;
	private double open;
	private double high;
	private double low;
	private double close;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getTradingSymbol() {
		return tradingSymbol;
	}
	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
@Override
	public String toString() {
		return "TIndexHistoricalData [id=" + id + ", timeStamp=" + timeStamp + ", tradingSymbol=" + tradingSymbol
				+ ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + "]";
	}
}
