package com.infinan.common.model;

import org.springframework.stereotype.Component;

@Component
public class TradeProfitCalculationModel {
	private String orderId;
	private String tradingSymbol;
	private String buyOrderExecutionTime;
	private double buyOrderRealPrice;
	private double sellOrderRealPrice;
	private double amountAvailableForTrade;
	private int lots;
	private double quantity;
	private double buyValue;
	private double profitAmount;
	private double profitPercentage;
	private double brokerage;
	private double taxes;
	private double netProfit;
	private double netProfitPercentage;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTradingSymbol() {
		return tradingSymbol;
	}
	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}
	public String getBuyOrderExecutionTime() {
		return buyOrderExecutionTime;
	}
	public void setBuyOrderExecutionTime(String buyOrderExecutionTime) {
		this.buyOrderExecutionTime = buyOrderExecutionTime;
	}
	public double getBuyOrderRealPrice() {
		return buyOrderRealPrice;
	}
	public void setBuyOrderRealPrice(double buyOrderRealPrice) {
		this.buyOrderRealPrice = buyOrderRealPrice;
	}
	public double getSellOrderRealPrice() {
		return sellOrderRealPrice;
	}
	public void setSellOrderRealPrice(double sellOrderRealPrice) {
		this.sellOrderRealPrice = sellOrderRealPrice;
	}
	public double getAmountAvailableForTrade() {
		return amountAvailableForTrade;
	}
	public void setAmountAvailableForTrade(double amountAvailableForTrade) {
		this.amountAvailableForTrade = amountAvailableForTrade;
	}
	public double getProfitAmount() {
		return profitAmount;
	}
	public void setProfitAmount(double profitAmount) {
		this.profitAmount = profitAmount;
	}
	public double getProfitPercentage() {
		return profitPercentage;
	}
	public void setProfitPercentage(double profitPercentage) {
		this.profitPercentage = profitPercentage;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getBuyValue() {
		return buyValue;
	}
	public void setBuyValue(double buyValue) {
		this.buyValue = buyValue;
	}
	public int getLots() {
		return lots;
	}
	public void setLots(int lots) {
		this.lots = lots;
	}
	public double getBrokerage() {
		return brokerage;
	}
	public void setBrokerage(double brokerage) {
		this.brokerage = brokerage;
	}
	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}
	public double getNetProfit() {
		return netProfit;
	}
	public void setNetProfit(double netProfit) {
		this.netProfit = netProfit;
	}
	public double getNetProfitPercentage() {
		return netProfitPercentage;
	}
	public void setNetProfitPercentage(double netProfitPercentage) {
		this.netProfitPercentage = netProfitPercentage;
	}
	
	@Override
	public String toString() {
		return "TradeProfitCalculationModel [orderId=" + orderId + ", tradingSymbol=" + tradingSymbol
				+ ", buyOrderExecutionTime=" + buyOrderExecutionTime + ", buyOrderRealPrice=" + buyOrderRealPrice
				+ ", sellOrderRealPrice=" + sellOrderRealPrice + ", amountAvailableForTrade=" + amountAvailableForTrade
				+ ", profitAmount=" + profitAmount + ", profitPercentage=" + profitPercentage + ", quantity=" + quantity
				+ ", lots=" + lots + ", brokerage=" + brokerage + ", taxes=" + taxes + ", netProfit=" + netProfit
				+ ", netProfitPercentage=" + netProfitPercentage + "]";
	}
}
