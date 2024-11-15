package com.infinan.common.model;

import org.springframework.stereotype.Component;

@Component
public class StrategyDataModel {
	private String buyOrderExecutionTime;
	private Double buyOrderRealPrice;
	private String orderId;
	private String quantity;
	private String sellOrderExecutionTime;
	private String sellOrderRealPrice;
	private String sellOrderPlaceTime;
	private String sellOrderCalaulatedPrice;
	private String sellOrderPriceModified = "False";
	public String getSellOrderExecutionTime() {
		return sellOrderExecutionTime;
	}
	public void setSellOrderExecutionTime(String sellOrderExecutionTime) {
		this.sellOrderExecutionTime = sellOrderExecutionTime;
	}
	public String getSellOrderRealPrice() {
		return sellOrderRealPrice;
	}
	public void setSellOrderRealPrice(String sellOrderRealPrice) {
		this.sellOrderRealPrice = sellOrderRealPrice;
	}
	public String getBuyOrderExecutionTime() {
		return buyOrderExecutionTime;
	}
	public void setBuyOrderExecutionTime(String buyOrderExecutionTime) {
		this.buyOrderExecutionTime = buyOrderExecutionTime;
	}
	public Double getBuyOrderRealPrice() {
		return buyOrderRealPrice;
	}
	public void setBuyOrderRealPrice(Double buyOrderRealPrice) {
		this.buyOrderRealPrice = buyOrderRealPrice;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getSellOrderPlaceTime() {
		return sellOrderPlaceTime;
	}
	public void setSellOrderPlaceTime(String sellOrderPlaceTime) {
		this.sellOrderPlaceTime = sellOrderPlaceTime;
	}
	public String getSellOrderCalaulatedPrice() {
		return sellOrderCalaulatedPrice;
	}
	public void setSellOrderCalaulatedPrice(String sellOrderCalaulatedPrice) {
		this.sellOrderCalaulatedPrice = sellOrderCalaulatedPrice;
	}
	public String getSellOrderPriceModified() {
		return sellOrderPriceModified;
	}
	public void setSellOrderPriceModified(String sellOrderPriceModified) {
		this.sellOrderPriceModified = sellOrderPriceModified;
	}
	@Override
	public String toString() {
		return "StrategyDataModel [sellOrderExecutionTime=" + sellOrderExecutionTime + ", sellOrderRealPrice="
				+ sellOrderRealPrice + ", buyOrderExecutionTime=" + buyOrderExecutionTime + ", buyOrderRealPrice="
				+ buyOrderRealPrice + ", orderId=" + orderId + ", quantity=" + quantity + ", sellOrderPlaceTime="
				+ sellOrderPlaceTime + ", sellOrderCalaulatedPrice=" + sellOrderCalaulatedPrice
				+ ", sellOrderPriceModified=" + sellOrderPriceModified + "]";
	}
	
}
