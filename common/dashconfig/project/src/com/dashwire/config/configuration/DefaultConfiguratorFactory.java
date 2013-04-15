package com.dashwire.config.configuration;

import com.dashwire.config.resources.*;

public class DefaultConfiguratorFactory implements ConfiguratorFactory{
	
	private DefaultConfiguratorFactory() { }
	private static DefaultConfiguratorFactory singleton = null;
	
	public static synchronized DefaultConfiguratorFactory getInstance() {
		if(singleton == null) singleton = new DefaultConfiguratorFactory();
		return singleton;
	}

	public ResourceConfigurator getResourceConfigurator(String nodeName) throws IllegalArgumentException {
		if("shortcuts".equals(nodeName)) {
			return new DefaultShortcutConfigurator();
			//Commenting it as the new RaHomeItemConfigurator is not used by Ready2Go product.
			//It is used only by touchwall product
            //return new RAWidgetsShortcutsConfiguratorInterface();
		} else if("wallpapers".equals(nodeName)) {
			return new RAWallpaperConfiguratorInterface();   
			//return new WallpaperConfigurator();                   
		} else if("ringtones".equals(nodeName)) {
			return new RARingtoneConfiguratorInterface();                   
		} else if("networks".equals(nodeName)) {
			return new RAWifiConfiguratorInterface();
		} else if("bookmarks".equals(nodeName)) {
			return new BookmarkConfigurator();                   
		} else if("contacts".equals(nodeName)) {
			return new RAContactsConfiguratorInterface();
		} else if("accounts".equals(nodeName)) {
			return new GoogleLoginServiceConfigurator();                   
		} else if("social".equals(nodeName)) {
            return new SocialNetworkConfigurator();                   
        } 
		throw new IllegalArgumentException("Resource: " + nodeName);
	}
}
