package com.dashwire.config.email;

import com.dashwire.config.configuration.BaseEmailService;
import com.dashwire.config.configuration.EmailCreator;

public class NewApiEmailService extends BaseEmailService {

	public NewApiEmailService() { 
		this("default");
	}
	
	public NewApiEmailService(String name) {
		super(name);
	}

	@Override
	public EmailCreator getEmailCreator() {
		return new SamsungEmailCreator();
	}

	
}
