package com.infinan.common.model;

import org.springframework.stereotype.Service;

@Service
public class StrategySupportingMethods {
	
	public double getProfitPercentage(StrategyMainDataModel strategyMainDataModel) {
		return getProfitAmount(strategyMainDataModel)/Double.parseDouble(strategyMainDataModel.getBuyOrderCalaulatedPrice())*1
				*100;
	}

	public double getProfitAmount(StrategyMainDataModel strategyMainDataModel) {
		return (Double.parseDouble(strategyMainDataModel.getSellOrderRealPrice()) - strategyMainDataModel.getBuyOrderRealPrice())
				*1;
	}
	
//	private Double calculateTrailingStoplossPercentage() {
	//	return (tradeBuyPrice-trailingStopLossPrice) 
	//			*100/tradeBuyPrice;
//	}
	
	public double getLiveProfitPercentage(StrategyMainDataModel strategyMainDataModel) {
		return getLiveProfitAmount(strategyMainDataModel)
		/(strategyMainDataModel.getBuyOrderRealPrice()
		*Double.parseDouble(strategyMainDataModel.getQuantity()))
		*100;	
	}	
	
	public Double getLiveProfitAmount(StrategyMainDataModel strategyMainDataModel) {
		return (Double.parseDouble(strategyMainDataModel.getSellOrderRealPrice()) 
				- strategyMainDataModel.getBuyOrderRealPrice())
				*Double.parseDouble(strategyMainDataModel.getQuantity());
	}	
}
