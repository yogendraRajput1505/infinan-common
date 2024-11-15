package com.infinan.common.model;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Order;

@Service
public class TradeExitPropertiesForStrategy {
	private List<HistoricalData> pastCandle;
	private HistoricalData currentCandleHistoricalData;
	private StrategyMainDataModel strategyMainDataModel;
	private List<Order> orders;
	
	public List<HistoricalData> getPastCandle() {
		return pastCandle;
	}
	public void setPastCandle(List<HistoricalData> pastCandle) {
		this.pastCandle = pastCandle;
	}
	public HistoricalData getCurrentCandleHistoricalData() {
		return currentCandleHistoricalData;
	}
	public void setCurrentCandleHistoricalData(HistoricalData currentCandleHistoricalData) {
		this.currentCandleHistoricalData = currentCandleHistoricalData;
	}
	public StrategyMainDataModel getstrategyMainDataModel() {
		return strategyMainDataModel;
	}
	public void setstrategyMainDataModel(
			StrategyMainDataModel strategyMainDataModel) {
		this.strategyMainDataModel = strategyMainDataModel;
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	@Override
	public String toString() {
		return "TradeExitPropertiesForStrategy [pastCandle=" + pastCandle + ", currentCandleHistoricalData="
				+ currentCandleHistoricalData + ", strategyMainDataModel=" + strategyMainDataModel + ", orders="
				+ orders + "]";
	}
}
