package com.dashwire.config.email;

import com.dashwire.config.configuration.BaseEmailService;

public class NewApiEmailService extends BaseEmailService {

	public NewApiEmailService() { 
		this("default");
	}
	
	public NewApiEmailService(String name) {
		super(name);
	}

	@Override
	public com.dashwire.config.configuration.EmailCreator getEmailCreator() {
		return new SonyEmailCreator();
	}
}
