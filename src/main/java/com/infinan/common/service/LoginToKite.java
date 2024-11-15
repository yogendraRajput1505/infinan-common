package com.infinan.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infinan.common.entity.KiteLoginCredentials;
import com.infinan.common.repo.KiteLoginCredentialsRepository;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Profile;
import com.zerodhatech.models.User;

@Service
public class LoginToKite {
	
	@Autowired
	private KiteLoginCredentialsRepository kiteLoginCredentialsRepo;
	
	protected static final Logger LOGGER = LoggerFactory.getLogger("LoginToKite");
	private KiteConnect kiteSdk;
	
	public KiteConnect getKiteConnect() {
		if(kiteSdk != null || loginOrNot()) {
			return kiteSdk;
		}
			
		throw new RuntimeException("Not Connected to Kite. Please Login Again");
	}	
	
	// check that access and public key is available in database or not
	private boolean loginOrNot(){
		Properties loadProperty = loadProperty("kiteConnectCredentials.properties");
		String api_key = loadProperty.getProperty("api-key");
		String userId= loadProperty.getProperty("kite.userId");
      
		kiteSdk = new KiteConnect(api_key);
		kiteSdk.setUserId(userId);
		Optional<KiteLoginCredentials> list=kiteLoginCredentialsRepo.findById(userId);
		if(list.isEmpty()) {
			return false;
		}else {
			KiteLoginCredentials credentials=list.get();
			
			String access_token=credentials.getAccessToken();
			String public_token=credentials.getPublicToken();
			
			kiteSdk.setAccessToken(access_token);
	        kiteSdk.setPublicToken(public_token);
		}
		return true;
	}

	public String connectToKite(String req_token){
		
		Properties loadProperty = loadProperty("kiteConnectCredentials.properties");
		String api_key = loadProperty.getProperty("api-key");
        String sec_key = loadProperty.getProperty("kite.api.secretKey");
        String userId= loadProperty.getProperty("kite.userId");
        
        kiteSdk = new KiteConnect(api_key);
        kiteSdk.setUserId(userId);
        User users = null;
        try {
             users = kiteSdk.generateSession(req_token, sec_key);
        }  catch (IOException | JSONException | KiteException e) {
        	LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
        } 
        
        String access_token=users.accessToken;
        String public_token=users.publicToken;
        kiteSdk.setAccessToken(access_token);
        kiteSdk.setPublicToken(public_token);
        
        KiteLoginCredentials kiteLoginCredentials=new KiteLoginCredentials();
        kiteLoginCredentials.setUserId(userId);
        kiteLoginCredentials.setAccessToken(access_token);
        kiteLoginCredentials.setPublicToken(public_token);
        
//        saveOrUpdateCredentials(kiteLoginCredentials);
        kiteLoginCredentialsRepo.save(kiteLoginCredentials);     
        Profile profile = null ;
        try {
            profile = kiteSdk.getProfile();
            return profile.userName;
            
        } catch (IOException | KiteException | JSONException e) {
        	LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
        }
        
        return "UserName not Loaded";
	}
	
//	private void saveOrUpdateCredentials(KiteLoginCredentials kiteLoginCredentials) {
//		
//		Optional<KiteLoginCredentials> findById = kiteLoginCredentialsRepo.findById(kiteLoginCredentials.getEmail());
//		
//		if(findById.isPresent()) {
//			kiteLoginCredentialsRepo.save(kiteLoginCredentials);
//		}
//		
//		//kiteLoginCredentialsRepo.deleteAll();
//        
//	}

	/* taking value from properties file */
	private Properties loadProperty(String propertyFileName) {
        try {
            Properties properties = new Properties();
            InputStream inputStream = LoginToKite.class.getClassLoader().getResourceAsStream(propertyFileName);
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
        	LOGGER.error("An error occured : "+e.getMessage());
			LOGGER.error("Stack trace: ", e);
            throw new RuntimeException("Failed to load property: " + propertyFileName);
        }
    }
	
	
}
