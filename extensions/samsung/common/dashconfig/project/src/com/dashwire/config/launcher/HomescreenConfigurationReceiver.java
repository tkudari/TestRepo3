package com.dashwire.config.launcher;

public class HomescreenConfigurationReceiver extends BaseHomescreenConfigurationReceiver {
	
	
	@Override
	public LauncherModel getLauncherModel() {
		return new SamsungLauncherModel(mContext);
	}
}
