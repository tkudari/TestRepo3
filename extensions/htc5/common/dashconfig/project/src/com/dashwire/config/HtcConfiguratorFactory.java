package com.dashwire.config;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.config.configuration.DefaultConfiguratorFactory;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.resources.FeedUtilitiesImpl;
import com.dashwire.config.resources.HtcFeedConfigurator;
import com.dashwire.config.resources.RAHTCWallpaperConfiguratorInterface;

public class HtcConfiguratorFactory implements ConfiguratorFactory {
	private HtcConfiguratorFactory() {
	}

	private static HtcConfiguratorFactory singleton = null;

	public static synchronized HtcConfiguratorFactory getInstance() {
		if (singleton == null)
			singleton = new HtcConfiguratorFactory();
		return singleton;
	}

	@Override
	public ResourceConfigurator getResourceConfigurator(String nodeName) throws IllegalArgumentException {
		if ("wallpapers".equals(nodeName)) {
            return new RAHTCWallpaperConfiguratorInterface();
		}
        else if ("htc_feeds".equals(nodeName)) {
            HtcFeedConfigurator feedConfigurator = new HtcFeedConfigurator();
            feedConfigurator.setFeedUtilities(new FeedUtilitiesImpl());
            return feedConfigurator;
        }
		else {
			return DefaultConfiguratorFactory.getInstance().getResourceConfigurator(nodeName);
		}
	}
}
