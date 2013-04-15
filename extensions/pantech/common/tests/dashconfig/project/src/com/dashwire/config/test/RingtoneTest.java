package com.dashwire.config.test;

import com.dashwire.config.resources.RingtoneConfigurator;

import android.media.RingtoneManager;
import android.net.Uri;
import android.test.AndroidTestCase;

public class RingtoneTest extends AndroidTestCase {

	public void testSetRingtone() throws Exception {
		Uri originalUri = RingtoneManager.getActualDefaultRingtoneUri(mContext,
				RingtoneManager.TYPE_RINGTONE);
		Uri testUri = Uri.parse("content://media/internal/audio/media/158");

		RingtoneConfigurator rc = new RingtoneConfigurator();
		rc.setContext(mContext);
		rc.setRingtone("call", testUri);

		assertEquals(RingtoneManager.getActualDefaultRingtoneUri(mContext,
				RingtoneManager.TYPE_RINGTONE), testUri);

		RingtoneManager.setActualDefaultRingtoneUri(mContext,
				RingtoneManager.TYPE_RINGTONE, originalUri);
	}
}
