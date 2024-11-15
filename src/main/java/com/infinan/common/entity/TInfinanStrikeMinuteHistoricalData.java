package com.infinan.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import org.springframework.stereotype.Component;


@Component
@Entity
public class TInfinanStrikeMinuteHistoricalData{
	/**
	 * Id will be unique combination of  - 
	 * timeStamp+tradingSymbol+volume 
	 */
	@Id
	private String id;
	private String timeStamp;
	private String tradingSymbol;
	private double open;
	private double high;
	private double low;
	private double close;
	private long volume;
	private long oi;
	private String atm_otm_itm;
	
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
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public long getOi() {
		return oi;
	}
	public void setOi(long oi) {
		this.oi = oi;
	}
	
	public String getAtm_otm_itm() {
		return atm_otm_itm;
	}
	public void setAtm_otm_itm(String atm_otm_itm) {
		this.atm_otm_itm = atm_otm_itm;
	}
	@Override
	public String toString() {
		return "TInfinanHistoricalData [id=" + id + ", timeStamp=" + timeStamp + ", tradingSymbol=" + tradingSymbol
				+ ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", volume=" + volume
				+ ", oi=" + oi + ", atm_otm_itm=" + atm_otm_itm + "]";
	}
}
