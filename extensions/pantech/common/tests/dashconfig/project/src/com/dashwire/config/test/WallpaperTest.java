package com.dashwire.config.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.WallpaperManager;
import android.graphics.drawable.BitmapDrawable;
import android.test.AndroidTestCase;

import com.dashwire.config.R;
import com.dashwire.config.resources.WallpaperConfigurator;
public class WallpaperTest extends AndroidTestCase {
	
	public void testSetWallpaper() throws Exception {
		
		BitmapDrawable originalWallpaper = (BitmapDrawable) WallpaperManager.getInstance( mContext ).getDrawable();
		
		WallpaperConfigurator wp = new WallpaperConfigurator();
		wp.setResourceWallpaper(mContext, R.drawable.android);
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 3000, 
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		FutureTask<Boolean> future = new FutureTask<Boolean>(
				new Callable<Boolean>() {
					public Boolean call() {
						try {
							while(!getWallpaperInfo().contains("res:com.dashwire.config:drawable/android")) {								
							}
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					}
				});
	    executor.execute(future);
		
		assertTrue(future.get(3000, TimeUnit.MILLISECONDS));
		
		WallpaperManager.getInstance( mContext ).setBitmap(originalWallpaper.getBitmap());
	}
	
	private String getWallpaperInfo() throws Exception {
		Writer writer = new StringWriter();
		FileInputStream fis = new FileInputStream(new File("/data/system/wallpaper_info.xml"));
		Reader reader = new BufferedReader(new InputStreamReader(fis));
		char[] buffer = new char[1024];
		int n;
		while( (n = reader.read(buffer)) != -1) {
			StringBuilder sb = new StringBuilder();
			sb.append(buffer);
			writer.write(buffer, 0, n);
		}		
		return writer.toString();
	}
}
