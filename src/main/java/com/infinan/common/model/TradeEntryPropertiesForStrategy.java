package com.infinan.common.model;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class TradeEntryPropertiesForStrategy {
	private boolean isPE;
	private String strike;
	private boolean isLive;
	private double stopLossPercentage;
	private double crossoverTime;
	private double volumeForCondition;
	private Map<String, List<Double>> requestBody;
	
//	@JsonProperty("isPE")
	public boolean isPE() {
		return isPE;
	}
//	@JsonProperty("isPE")
	public void setIsPE(boolean isPE) {
		this.isPE = isPE;
	}
	public String getStrike() {
		return strike;
	}
	public void setStrike(String strike) {
		this.strike = strike;
	}
//	@JsonProperty("isLive")
	public boolean isLive() {
		return isLive;
	}
//	@JsonProperty("isLive")
	public void setIsLive(boolean isLive) {
		this.isLive = isLive;
	}
	public double getStopLossPercentage() {
		return stopLossPercentage;
	}
	public void setStopLossPercentage(double stopLossPercentage) {
		this.stopLossPercentage = stopLossPercentage;
	}
	public double getCrossoverTime() {
		return crossoverTime;
	}
	public void setCrossoverTime(double crossoverTime) {
		this.crossoverTime = crossoverTime;
	}
	public double getVolumeForCondition() {
		return volumeForCondition;
	}
	public void setVolumeForCondition(double volumeForCondition) {
		this.volumeForCondition = volumeForCondition;
	}
	public Map<String, List<Double>> getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(Map<String, List<Double>> requestBody) {
		this.requestBody = requestBody;
	}
	
	@Override
	public String toString() {
		return "TradeEntryPropertiesForStrategy [isPE=" + isPE + ", strike=" + strike + ", isLive=" + isLive
				+ ", stopLossPercentage=" + stopLossPercentage + ", crossoverTime=" + crossoverTime
				+ ", volumeForCondition=" + volumeForCondition + ", requestBody=" + requestBody + "]";
	}	
}
