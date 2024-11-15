package com.infinan.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateStrategyClassObject {
	protected static final Logger LOGGER = LoggerFactory.getLogger("CreateStrategyClassObject");
	public static Object getSellObjectClassObject(String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
		}
		
		return null;
	}
}
