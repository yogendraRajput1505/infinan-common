package com.infinan.common.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;



@Entity
@Component
public class StockList {
	
	@Id
	@Column(name = "name")
	private String name;
	
	@Column(name = "indices")
	private String indices;
	
	@Column(name = "exchange")
	private String exchange;
	
	@Column(name = "instrumentType")
	private String instrumentType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndices() {
		return indices;
	}

	public void setIndices(String indices) {
		this.indices = indices;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getInstrumentType() {
		return instrumentType;
	}

	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}

	@Override
	public String toString() {
		return "StocksList [name=" + name + ", indices=" + indices + ", exchange=" + exchange + ", instrumentType="
				+ instrumentType + "]";
	}
	
}
