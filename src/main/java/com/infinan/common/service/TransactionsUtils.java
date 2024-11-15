package com.infinan.common.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TransactionsUtils {
	
	private final int BANKNIFTY_MAX_QUANTITY;
	private final int NIFTY50_MAX_QUANTITY;
	private final int MIDCPNIFTY_MAX_QUANTITY;
	private final int FINNIFTY_MAX_QUANTITY;
	private final int SENSEX_MAX_QUANTITY;
	
	public TransactionsUtils(Environment environment){
		BANKNIFTY_MAX_QUANTITY =	Integer.parseInt(environment.getProperty("index.lot.max.BANKNIFTY"))*Integer.parseInt(environment.getProperty("index.lot.size.BANKNIFTY"));
        NIFTY50_MAX_QUANTITY =	Integer.parseInt(environment.getProperty("index.lot.max.NIFTY"))*Integer.parseInt(environment.getProperty("index.lot.size.NIFTY"));
        MIDCPNIFTY_MAX_QUANTITY =	Integer.parseInt(environment.getProperty("index.lot.max.MIDCPNIFTY"))*Integer.parseInt(environment.getProperty("index.lot.size.MIDCPNIFTY"));
        FINNIFTY_MAX_QUANTITY =	Integer.parseInt(environment.getProperty("index.lot.max.FINNIFTY"))*Integer.parseInt(environment.getProperty("index.lot.size.FINNIFTY"));    	
        SENSEX_MAX_QUANTITY =	Integer.parseInt(environment.getProperty("index.lot.max.SENSEX"))*Integer.parseInt(environment.getProperty("index.lot.size.SENSEX"));
	}
	
	public double calculateTaxes(double buyAmount, double sellAmount, double brokerage) {
		return getSTT(sellAmount) 
				+ getTransactionCharges(buyAmount, sellAmount)
				+ getGst(brokerage, getTransactionCharges(buyAmount, sellAmount))
				+ getSebiCharges(buyAmount, sellAmount)
				+ getStampCharges(buyAmount);
	}
	
	public double getSTT(double sellAmount) {
	    return sellAmount * 0.000625;
	}

	public double getTransactionCharges(double buyAmount, double sellAmount) {
	    return buyAmount * 0.0005 + sellAmount * 0.0005;
	}
	
	public int getBrokerageForOneOrder() {
		return 20+20;
	}
	
	public double getGst(double brokerage, double transactionCharge) {
		return (brokerage+transactionCharge)*0.18;
	}
	
	public double getSebiCharges(double buyAmount, double sellAmount) {
		return buyAmount * 0.000001 + sellAmount * 0.000001;
	}
	
	public double getStampCharges(double buyAmount) {
		return buyAmount * 0.00002;
	}
	
	/**
	 * There can me multiple for one trade because there are restriction for quantity on one order
	 * @param quantity
	 * @param tradingSymbol
	 * @return
	 */
	public int getBrokerageForOneTrade(double quantity, String tradingSymbol) {
		 int numberofOrders = (int)Math.ceil((float)quantity/findMaxQuantityAllowedPerOrder(tradingSymbol));
		 return numberofOrders*getBrokerageForOneOrder();
	}
	
	public int findMaxQuantityAllowedPerOrder(String tradingSymbol) {
		int maxQuantityAllowedPerOrder = 1;
		if(tradingSymbol.startsWith("BANK")) {
			maxQuantityAllowedPerOrder = BANKNIFTY_MAX_QUANTITY;
		}else if(tradingSymbol.startsWith("NIFTY")) {
			maxQuantityAllowedPerOrder = NIFTY50_MAX_QUANTITY;
		}else if(tradingSymbol.startsWith("FIN")) {
			maxQuantityAllowedPerOrder = FINNIFTY_MAX_QUANTITY;
		}else if(tradingSymbol.startsWith("MID")) {
			maxQuantityAllowedPerOrder = MIDCPNIFTY_MAX_QUANTITY;
		}else if(tradingSymbol.startsWith("SEN")) {
			maxQuantityAllowedPerOrder = SENSEX_MAX_QUANTITY;
		}
		return maxQuantityAllowedPerOrder;
	}
}
