package com.infinan.common.service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.infinan.common.model.StrategyDataModel;
import com.infinan.common.utils.CommonUtils;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Quote;

@Component
@Service
public class CustomBuySellService {

	@Autowired
	LoginToKite kite;
	
//	@Autowired
//	HftEmaStrategyData hftEmaStrategyData;
	
	@Autowired
	StockUtils stockUtils;
	
	@Autowired
	TransactionsUtils transactionsUtils;
	
	@Autowired
	M_HistoricalDataService m_HistoricalDataService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomBuySellService.class);
	
		
	private static final String BANKNIFTY = "BANKNIFTY";
	private static final String NIFTY50 = "NIFTY";
	private static final String FINNIFTY = "FINNIFTY";
	private static final String MIDCPNIFTY = "MIDCPNIFTY";
	private static final String SENSEX = "SENSEX";
	private final int WAITING_TIME_FOR_BUY_ORDER_CANCELLATION; //In Seconds
	
	public CustomBuySellService(Environment environment) {
        WAITING_TIME_FOR_BUY_ORDER_CANCELLATION = Integer.parseInt(environment.getProperty("HftEmaStrategy.waitingTimeForBuyOrderCancellation"));
    }

	public Map<String, Quote> getScripData(String name){
		String[] arr = {name};
		Map<String, Quote> quotes = null;
		try {
			quotes = kite.getKiteConnect().getQuote(arr);			
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return quotes;
	}
	
	
		
	//For Now We are considering an order with max quantity mentioned in application.prop for different indexes
	// This will place buy and sell order with given target
	public List<Order> prepareForPlaceOrder(String tradingSymbol, int lot, double buyPrice, double targetPercentage, boolean chooseMarketPrice, StrategyDataModel strategyDataModel) {
		int quantity = stockUtils.getQuantity(tradingSymbol,lot);
		List<Order> currentBuyOrders = new ArrayList<Order>();
		List<Order> currentSellOrders = new ArrayList<Order>();
		
		//Will check and confirm if below commented method is required or not
		//openOrders(currentSellOrders,hftEmaStrategyCsvDataModel);
		try {
			currentBuyOrders = placeOrder(tradingSymbol, quantity, buyPrice, targetPercentage, chooseMarketPrice);			
		}catch(Exception e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		//Buy Order Placed Time, Write Logic Here
		 long buyOrderPlacedTime = LocalTime.now().toSecondOfDay();
		for(; ;) {
			try {
				List<Order> orders = kite.getKiteConnect().getOrders();
				int count=0;
				for(Order order : orders){
					for(Order currentOrder:currentBuyOrders) {
						if(order.orderId.equals(currentOrder.orderId) && (order.status.equals("CANCELLED") || order.status.equals("REJECTED"))) {
							count++;
						}						
						if(order.orderId.equals(currentOrder.orderId) && order.status.equals("COMPLETE")){
							count++; 
							currentSellOrders.add(exitFromOrder(tradingSymbol, Integer.parseInt(order.quantity), Double.parseDouble(order.averagePrice), targetPercentage,strategyDataModel));
							//Need to modify format of date
							
							strategyDataModel.setBuyOrderExecutionTime(CommonUtils.getOffsetDateTime(order.orderTimestamp.toString(), "EEE MMM dd HH:mm:ss zzz yyyy"));
							strategyDataModel.setBuyOrderRealPrice(Double.parseDouble(order.averagePrice));
							strategyDataModel.setQuantity(order.quantity);
							strategyDataModel.setOrderId(order.orderId);
							// Sell Order Place time should be similar to Buy Order execution time, because sell Order will be placed right after buy order
							strategyDataModel.setSellOrderPlaceTime(order.orderTimestamp.toString());
						}
						// If Buy order is placed from 20 sec and does not executed yet then it will be cancelled
						else if(order.orderId.equals(currentOrder.orderId) && order.status.equals("OPEN") && LocalTime.now().toSecondOfDay() - buyOrderPlacedTime > WAITING_TIME_FOR_BUY_ORDER_CANCELLATION) {
							cancelOrders(currentBuyOrders, Constants.VARIETY_REGULAR);	
							return currentSellOrders;
						}
					}
				}
				
				if(count>= currentBuyOrders.size()) {
					return currentSellOrders;
				}
			} catch (JSONException | IOException | KiteException e) {
				LOGGER.error("An error occured : "+e.getMessage());
				LOGGER.error("Stack trace: ", e);
			}
			LOGGER.info("Buy Order is Placed, Waiting for Execution...");
		}
	}
	
	//For Now We are considering an order with max quantity mentioned in application.prop for different indexes
	// This will place buy order with given target
	public List<Order> prepareForBuyOrder(String tradingSymbol, int lot, double buyPrice, boolean chooseMarketPrice, StrategyDataModel hftEmaStrategyCsvDataModel) {
		int quantity = stockUtils.getQuantity(tradingSymbol,lot);
		List<Order> currentBuyOrders = new ArrayList<Order>();
		//List<Order> currentSellOrders = new ArrayList<Order>();
		
		//Will check and confirm if below commented method is required or not
		//openOrders(currentSellOrders,hftEmaStrategyCsvDataModel);
		try {
			currentBuyOrders = placeOrder(tradingSymbol, quantity, buyPrice, 0, chooseMarketPrice);			
		}catch(Exception e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		//Buy Order Placed Time, Write Logic Here
		 long buyOrderPlacedTime = LocalTime.now().toSecondOfDay();
		for(; ;) {
			try {
				List<Order> orders = kite.getKiteConnect().getOrders();
				int count=0;
				for(Order order : orders){
					for(Order currentOrder:currentBuyOrders) {
						if(order.orderId.equals(currentOrder.orderId) && (order.status.equals("CANCELLED") || order.status.equals("REJECTED"))) {
							count++;
						}						
						if(order.orderId.equals(currentOrder.orderId) && order.status.equals("COMPLETE")){
							count++; 
							//currentSellOrders.add(exitFromOrder(tradingSymbol, Integer.parseInt(order.quantity), Double.parseDouble(order.averagePrice), targetPercentage,hftEmaStrategyCsvDataModel));
							//Need to modify format of date
							
							//If Multiple orders then these values will be differ and we are considering the last value;
							hftEmaStrategyCsvDataModel.setBuyOrderExecutionTime(CommonUtils.getOffsetDateTime(order.orderTimestamp.toString(), "EEE MMM dd HH:mm:ss zzz yyyy"));
							hftEmaStrategyCsvDataModel.setBuyOrderRealPrice(Double.parseDouble(order.averagePrice));
							hftEmaStrategyCsvDataModel.setQuantity(order.quantity);
							hftEmaStrategyCsvDataModel.setOrderId(order.orderId);
							// Sell Order Place time should be similar to Buy Order execution time, because sell Order will be placed right after buy order
//							hftEmaStrategyCsvDataModel.setSellOrderPlaceTime(order.orderTimestamp.toString());
						}
						// If Buy order is placed from 30 sec and does not executed yet then it will be cancelled
						//As of now this condition seems good for Buy Order execution
						else if(order.orderId.equals(currentOrder.orderId) && order.status.equals("OPEN") && LocalTime.now().toSecondOfDay() - buyOrderPlacedTime > WAITING_TIME_FOR_BUY_ORDER_CANCELLATION) {
							cancelOrders(currentBuyOrders, Constants.VARIETY_REGULAR);		
							return currentBuyOrders;
						}
					}
				}
				
				if(count>= currentBuyOrders.size()) {
					return currentBuyOrders;
				}
			} catch (JSONException | IOException | KiteException e) {
				LOGGER.error("An error occured : "+e.getMessage());
				LOGGER.error("Stack trace: ", e);
			}
			LOGGER.info("Buy Orders are Executed");
//			System.out.println("Buy Orders are Executed");
		}
	}
	
		//For Now We are considering an order with max quantity mentioned in application.prop for different indexes
		// This will place sell order with given target
		public List<Order> prepareForSellOrder(List<Order> currentBuyOrders, double targetPercentage, StrategyDataModel hftEmaStrategyCsvDataModel) {
			List<Order> currentSellOrders = new ArrayList<Order>();
			for(; ;) {
				try {
					List<Order> orders = kite.getKiteConnect().getOrders();
					int count=0;
					//Rewrite these for loop logic, we can check if currentBuyOrders's order is available in orders List. so we will reduce loop count;
					for(Order order : orders){
						for(Order currentOrder:currentBuyOrders) {
							if(order.orderId.equals(currentOrder.orderId) && (order.status.equals("CANCELLED") || order.status.equals("REJECTED"))) {
								count++;
							}						
							if(order.orderId.equals(currentOrder.orderId) && order.status.equals("COMPLETE")){
								count++; 
								//check if currentOrder.tradingSymbol is giving expected value or not
								currentSellOrders.add(exitFromOrder(currentOrder.tradingSymbol, Integer.parseInt(order.quantity), Double.parseDouble(order.averagePrice), targetPercentage,hftEmaStrategyCsvDataModel));
							}
						}
					}
					
					if(count>= currentBuyOrders.size()) {
						return currentSellOrders;
					}
				} catch (JSONException | IOException | KiteException e) {
					LOGGER.error("An error occured : "+e.getMessage());
					LOGGER.error("Stack trace: ", e);
				}
				LOGGER.info("Buy Orders are Executed");
//				System.out.println("Sell Orders are Placed");
			}
		}
	
		//Target % is not required. After removing prepareForPlaceOrder() remove this unused parameter also
	private List<Order> placeOrder(String tradingSymbol, int quantity, double buyPrice, double targetPercentage, boolean chooseMarketPrice) {
		String orderType = Constants.ORDER_TYPE_LIMIT;
		//If OrderType is Market then buyPrice should not play any role (need to confirm)
		if(chooseMarketPrice)
			orderType = Constants.ORDER_TYPE_MARKET;
		
		OrderParams orderParams = new OrderParams();
		//	orderParams.
		orderParams.quantity = quantity;
		orderParams.orderType = orderType;
		orderParams.tradingsymbol = tradingSymbol;
		orderParams.product = Constants.PRODUCT_MIS;
		orderParams.validity = Constants.VALIDITY_DAY;
		if(tradingSymbol.startsWith("SENSEX")) {
			orderParams.exchange = Constants.EXCHANGE_BFO;
		}
		else {
			orderParams.exchange = Constants.EXCHANGE_NFO;
		}
		orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
		orderParams.price = stockUtils.roundToNearestMultiple(buyPrice,0.05);
		orderParams.triggerPrice = 0.0;
		orderParams.tag = "HFT_EMA";	
		
//		hftEmaStrategyData.setBuyOrderCalaulatedPrice(orderParams.price);
		return chooseIcebregOrRegular(tradingSymbol, quantity, orderParams);		
	}

	//Need to verify the Quantity for Multiple Orders
	private List<Order> chooseIcebregOrRegular(String tradingSymbol, int quantity, OrderParams orderParams) {
		int maxQuantityAllowedPerOrder = transactionsUtils.findMaxQuantityAllowedPerOrder(tradingSymbol);
		List<Order> placedOrder = new ArrayList<Order>();
		if(quantity > maxQuantityAllowedPerOrder) {
			int noOfOrder = (int)Math.ceil((float)quantity/maxQuantityAllowedPerOrder);
			int newQuantity = Math.round(quantity/noOfOrder/stockUtils.getLotSize(tradingSymbol))*stockUtils.getLotSize(tradingSymbol);				
			orderParams.quantity = newQuantity;
			for(int i=0;i<noOfOrder;i++) {
				placedOrder.add(placeOrderToKite(orderParams, Constants.VARIETY_REGULAR));
			}
			return placedOrder;
		}
		placedOrder.add(placeOrderToKite(orderParams, Constants.VARIETY_REGULAR));
		return placedOrder;
	}
		
	private Order exitFromOrder(String tradingSymbol, int quantity, double buyPrice, double targetPercentage, StrategyDataModel hftEmaStrategyCsvDataModel) {
		OrderParams orderParams = new OrderParams();
		//	orderParams.
		orderParams.quantity = quantity;
		orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
		orderParams.tradingsymbol = tradingSymbol;
		orderParams.product = Constants.PRODUCT_MIS;
		if(tradingSymbol.startsWith(SENSEX))
			orderParams.exchange = Constants.EXCHANGE_BFO;
		else
			orderParams.exchange = Constants.EXCHANGE_NFO;
		orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;
		orderParams.validity = Constants.VALIDITY_DAY;
		
		buyPrice = getAdjustedPrice(tradingSymbol, buyPrice, targetPercentage);
		
		
		orderParams.price = stockUtils.roundToNearestMultiple(buyPrice, 0.05);
		orderParams.triggerPrice = 0.0;
		orderParams.tag = "HFT_EMA";
		hftEmaStrategyCsvDataModel.setSellOrderCalaulatedPrice(orderParams.price+"");
		return placeOrderToKite(orderParams, Constants.VARIETY_REGULAR);
	}

	private double getAdjustedPrice(String tradingSymbol, double buyPrice, double targetPercentage) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -1);
			Date fromDate = cal.getTime();
			List<HistoricalData> dataArrayList = kite.getKiteConnect().getHistoricalData(fromDate, new Date(), m_HistoricalDataService.getInstrumentToken(tradingSymbol), "day", false, true).dataArrayList;
			double oneYearHighPrice = dataArrayList.stream()
	                .max(Comparator.comparingDouble(h -> h.high))
	                .orElseThrow(() -> new RuntimeException("No historical data available")).high;
	       
			buyPrice = buyPrice+getTargetPrice(buyPrice, targetPercentage);
			if(buyPrice>=oneYearHighPrice) {
				buyPrice = oneYearHighPrice*0.95;//doing -1 just to make sure sell order will not fail
			}
			
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return buyPrice;
	}
	
	public List<Order> checkOrderStatus(String orderId) {
		List<Order> orders = new ArrayList<Order>();
		try {
			orders = kite.getKiteConnect().getOrderHistory(orderId);
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return orders;
	}
	
	//It will be good if we return OrderId and status in a Map
	public Map<String, String> openOrders(List<Order> sellOrders, StrategyDataModel hftEmaStrategyCsvDataModel) {
		List<Order> allOrders = new ArrayList<Order>();
		Map<String,String> openSellOrder = new HashMap<>();
		try {
			// We are not using getOrderHistory so that network round should reduced.
			allOrders = kite.getKiteConnect().getOrders();
			
			for(Order order : allOrders) {
				for(Order sellOrder : sellOrders) {
					if(order.orderId.equals(sellOrder.orderId) && order.status.equalsIgnoreCase("OPEN")){
						openSellOrder.put(sellOrder.orderId, "OPEN");
					}
					if(order.orderId.equals(sellOrder.orderId) && order.status.equalsIgnoreCase("COMPLETE")) {
						//Need to modify Date Format
						hftEmaStrategyCsvDataModel.setSellOrderExecutionTime(order.orderTimestamp.toString());
						hftEmaStrategyCsvDataModel.setSellOrderRealPrice(order.averagePrice);
					}
				}
			}
			
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		return openSellOrder;
	}
	
	public double getTargetPrice(double buyPrice, double targetPercentage) {
		return stockUtils.roundToNearestMultiple(targetPercentage/100*buyPrice, 0.05);
	}
	
	private Order placeOrderToKite(OrderParams orderParams, String variety ) {		
		Order order = null;
		try {
			LOGGER.info("Type : "+orderParams.transactionType+
						", Id : "+orderParams.orderType+
						", Price : "+orderParams.price+
						", Quantity : "+orderParams.quantity);
			order = kite.getKiteConnect().placeOrder(orderParams, variety);
			
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		
		return order;
	// System.out.println(order.orderId);
	}
	
	//2nd Perameter need to modify
	private void cancelOrder(String orderId, String variety) {
		try {
			kite.getKiteConnect().cancelOrder(orderId, variety);
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
	}
	
	private void cancelOrders(List<Order> currentBuyOrders, String variety) {
		try {
			for(Order order : currentBuyOrders) {
				kite.getKiteConnect().cancelOrder(order.orderId, variety);
			}
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
	}
	
	
	
	public void modifyOrder(String orderId, OrderParams orderParams, String variety) {
		try {
			kite.getKiteConnect().modifyOrder(orderId, orderParams, variety);
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
	}
	
	public void modifyOrders(List<String> orderIds, OrderParams orderParams, String variety) {
		try {
			for(String orderId : orderIds) {
				kite.getKiteConnect().modifyOrder(orderId, orderParams, variety);				
			}
		} catch (JSONException | IOException | KiteException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
	}

}
